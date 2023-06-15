package de.cubbossa.pathfinder.editmode.menu;

import de.cubbossa.menuframework.inventory.Action;
import de.cubbossa.menuframework.inventory.Button;
import de.cubbossa.menuframework.inventory.MenuPresets;
import de.cubbossa.menuframework.inventory.implementations.BottomInventoryMenu;
import de.cubbossa.menuframework.inventory.implementations.ListMenu;
import de.cubbossa.pathapi.PathFinderConfig;
import de.cubbossa.pathapi.PathFinderProvider;
import de.cubbossa.pathapi.event.NodeDeleteEvent;
import de.cubbossa.pathapi.group.DiscoverableModifier;
import de.cubbossa.pathapi.group.NodeGroup;
import de.cubbossa.pathapi.misc.NamespacedKey;
import de.cubbossa.pathapi.misc.PathPlayer;
import de.cubbossa.pathapi.node.Groupable;
import de.cubbossa.pathapi.node.Node;
import de.cubbossa.pathapi.node.NodeType;
import de.cubbossa.pathapi.storage.Storage;
import de.cubbossa.pathfinder.BukkitPathFinder;
import de.cubbossa.pathfinder.CommonPathFinder;
import de.cubbossa.pathfinder.PathFinderPlugin;
import de.cubbossa.pathfinder.editmode.DefaultNodeGroupEditor;
import de.cubbossa.pathfinder.editmode.renderer.EdgeArmorStandRenderer;
import de.cubbossa.pathfinder.editmode.renderer.NodeArmorStandRenderer;
import de.cubbossa.pathfinder.editmode.utils.ItemStackUtils;
import de.cubbossa.pathfinder.messages.Messages;
import de.cubbossa.pathfinder.util.BukkitUtils;
import de.cubbossa.pathfinder.util.BukkitVectorUtils;
import de.cubbossa.pathfinder.util.LocalizedItem;
import de.cubbossa.pathfinder.util.VectorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EditModeMenu {

  private final Storage storage;
  private final NamespacedKey key;
  private final Collection<NamespacedKey> multiTool = new HashSet<>();
  private final Collection<NodeType<?>> types;
  private Boolean undirectedEdgesMode;
  private UUID chainEdgeStart = null;

  public EditModeMenu(Storage storage, NamespacedKey group, Collection<NodeType<?>> types, PathFinderConfig.EditModeConfig config) {
    this.storage = storage;
    this.key = group;
    this.types = types;
    this.undirectedEdgesMode = !config.isDirectedEdgesByDefault();

    PathFinderProvider.get().getEventDispatcher().listen(NodeDeleteEvent.class, e -> {
      if (chainEdgeStart.equals(e.getNode().getNodeId())) {
        chainEdgeStart = null;
      }
    });
  }

  public BottomInventoryMenu createHotbarMenu(DefaultNodeGroupEditor editor, Player editingPlayer) {
    BottomInventoryMenu menu = new BottomInventoryMenu(0, 1, 2, 3);

    menu.setDefaultClickHandler(Action.HOTBAR_DROP, c -> {
      Bukkit.getScheduler().runTaskLater(PathFinderPlugin.getInstance(),
          () -> editor.setEditMode(BukkitUtils.wrap(c.getPlayer()), false), 1L);
    });

    menu.setButton(0, Button.builder()
        .withItemStack(() -> {
          ItemStack stack = new ItemStack(Material.FIREWORK_STAR);
          FireworkEffectMeta meta = (FireworkEffectMeta) stack.getItemMeta();
          meta.addItemFlags(ItemFlag.values());
          meta.setEffect(FireworkEffect.builder()
              // green = no current chain, orange = chain started
              .withColor(Color.fromRGB(chainEdgeStart == null ? 0x00ff00 : 0xfc8a00))
              .build());
          stack.setItemMeta(meta);

          return new LocalizedItem(stack, Messages.E_NODE_TOOL_N, Messages.E_NODE_TOOL_L).createItem(editingPlayer);
        })

        .withClickHandler(NodeArmorStandRenderer.LEFT_CLICK_NODE, context -> {
          Player p = context.getPlayer();
          storage.deleteNodes(List.of(context.getTarget().getNodeId()))
              .thenRun(() -> p.playSound(p.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1));
        })

        .withClickHandler(Action.LEFT_CLICK_AIR, context -> {
          PathPlayer<Player> p = CommonPathFinder.getInstance().wrap(context.getPlayer());
          // cancel chain
          if (chainEdgeStart != null) {
            p.sendMessage(Messages.E_NODE_CHAIN_NEW);
            chainEdgeStart = null;
            context.getMenu().refresh(context.getSlot());
            return;
          }

          Player player = context.getPlayer();

          undirectedEdgesMode = !undirectedEdgesMode;
          BukkitUtils.wrap(player).sendMessage(Messages.E_NODE_TOOL_DIR_TOGGLE.formatted(
              Messages.formatter().choice("value", !undirectedEdgesMode)
          ));
          context.getMenu().refresh(context.getSlot());
        })

        .withClickHandler(NodeArmorStandRenderer.RIGHT_CLICK_NODE, context -> {
          Player p = context.getPlayer();
          PathPlayer<Player> pp = CommonPathFinder.getInstance().wrap(p);
          if (chainEdgeStart == null) {
            chainEdgeStart = context.getTarget().getNodeId();
            context.getMenu().refresh(context.getSlot());
            pp.sendMessage(Messages.E_NODE_CHAIN_START);
            return;
          }
          if (chainEdgeStart.equals(context.getTarget().getNodeId())) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
          }
          Collection<CompletableFuture<?>> futures = new HashSet<>();
          futures.add(storage.modifyNode(chainEdgeStart, node -> {
            node.connect(context.getTarget().getNodeId());
          }));
          if (undirectedEdgesMode) {
            futures.add(storage.modifyNode(context.getTarget().getNodeId(), node -> {
              node.connect(chainEdgeStart);
            }));
          }
          CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenRun(() -> {
            chainEdgeStart = null;
            context.getMenu().refresh(context.getSlot());
            CommonPathFinder.getInstance().wrap(context.getPlayer()).sendMessage(Messages.E_NODE_CHAIN_NEW);
          }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
          });
        })

        .withClickHandler(Action.RIGHT_CLICK_BLOCK, context -> {

          Location view = context.getPlayer().getEyeLocation();
          Location block = context.getTarget().getLocation();
          BukkitVectorUtils.Orientation orientation = BukkitVectorUtils.getIntersection(view.toVector(), view.getDirection(), block.toVector());
          Location pos = BukkitVectorUtils.toBukkit(VectorUtils.snap(BukkitVectorUtils.toInternal(orientation.location()), 2))
              .toLocation(block.getWorld()).add(orientation.direction().clone().multiply(.5f));
          if (types.size() > 1) {
            openNodeTypeMenu(context.getPlayer(), pos);
            return;
          }

          NodeType<?> type = types.stream().findAny().orElse(null);
          if (type == null) {
            throw new IllegalStateException("Could not find any node type to generate node.");
          }

          storage
              .createAndLoadNode(type, BukkitVectorUtils.toInternal(pos))
              .thenCompose(node -> storage.modifyNode(node.getNodeId(), n -> {
                if (chainEdgeStart != null) {
                  storage.modifyNode(chainEdgeStart, o -> {
                    o.connect(node);
                  });
                  if (undirectedEdgesMode) {
                    n.connect(chainEdgeStart);
                  }
                }
                chainEdgeStart = n.getNodeId();
                if (!(n instanceof Groupable groupable)) {
                  return;
                }
                groupable.addGroup(storage.loadGroup(key).join().orElseThrow());
                groupable.addGroup(storage.loadGroup(CommonPathFinder.globalGroupKey()).join().orElseThrow());
              }))
              .exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
              });
        })

        .withClickHandler(EdgeArmorStandRenderer.LEFT_CLICK_EDGE, context -> {
          storage.modifyNode(context.getTarget().getStart(), node -> {
            node.disconnect(context.getTarget().getEnd());
          }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
          });
        })
    );

    menu.setButton(3, Button.builder()
        .withItemStack(new LocalizedItem(Material.ENDER_PEARL, Messages.E_TP_TOOL_N,
            Messages.E_TP_TOOL_L).createItem(editingPlayer))
        .withClickHandler(context -> {
          storage.loadGroup(key)
              .thenCompose(group -> storage.loadNodes(group.map(g -> (Collection<UUID>) g).orElseGet(HashSet::new)))
              .thenAccept(nodes -> {
                Player p = context.getPlayer();
                if (nodes.size() == 0) {
                  // no nodes in the current editing
                  p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                  return;
                }

                double dist = -1;
                Node nearest = null;
                Location pLoc = context.getPlayer().getLocation();
                for (Node node : nodes) {
                  double d = node.getLocation().distance(BukkitVectorUtils.toInternal(pLoc));
                  if (dist == -1 || d < dist) {
                    nearest = node;
                    dist = d;
                  }
                }

                Location newLoc = BukkitVectorUtils.toBukkit(nearest.getLocation()).setDirection(p.getLocation().getDirection());
                Bukkit.getScheduler().runTask(PathFinderPlugin.getInstance(), () -> {
                  p.teleport(newLoc);
                  p.playSound(newLoc, Sound.ENTITY_FOX_TELEPORT, 1, 1);
                });
              }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
              });
        }, Action.RIGHT_CLICK_ENTITY, Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR));

    menu.setButton(1, Button.builder()
        .withItemStack(new LocalizedItem(Material.CHEST, Messages.E_GROUP_TOOL_N,
            Messages.E_GROUP_TOOL_L).createItem(editingPlayer))
        .withClickHandler(NodeArmorStandRenderer.RIGHT_CLICK_NODE, context -> {
          storage.loadNode(context.getTarget().getNodeId()).thenAccept(node -> {
            if (node.isPresent() && node.get() instanceof Groupable groupable) {
              openGroupMenu(context.getPlayer(), groupable);
            }
          });
        })
        .withClickHandler(NodeArmorStandRenderer.LEFT_CLICK_NODE, context -> {
          storage.modifyNode(context.getTarget().getNodeId(), node -> {
            if (!(node instanceof Groupable groupable)) {
              return;
            }
            if (groupable.getGroups().isEmpty()) {
              return;
            }
            groupable.clearGroups();
            context.getPlayer().playSound(context.getPlayer().getLocation(),
                Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
          });
        }));

    menu.setButton(2, Button.builder()
        .withItemStack(new LocalizedItem(Material.ENDER_CHEST, Messages.E_MULTI_GROUP_TOOL_N,
            Messages.E_MULTI_GROUP_TOOL_L).createItem(editingPlayer))
        .withClickHandler(NodeArmorStandRenderer.RIGHT_CLICK_NODE, context -> {
          storage.modifyNode(context.getTarget().getNodeId(), node -> {
            if (!(node instanceof Groupable groupable)) {
              return;
            }
            storage.loadGroups(multiTool).thenAccept(groups -> {
              groups.forEach(groupable::addGroup);
            });
            context.getPlayer().playSound(context.getPlayer().getLocation(),
                Sound.BLOCK_CHEST_CLOSE, 1, 1);
          });
        })
        .withClickHandler(NodeArmorStandRenderer.LEFT_CLICK_NODE, context -> {
          storage.modifyNode(context.getTarget().getNodeId(), node -> {
            if (!(node instanceof Groupable groupable)) {
              return;
            }
            multiTool.forEach(groupable::removeGroup);
            context.getPlayer().playSound(context.getPlayer().getLocation(),
                Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
          });
        })
        .withClickHandler(Action.RIGHT_CLICK_AIR,
            context -> openMultiToolMenu(context.getPlayer())));
    return menu;
  }

  private void openGroupMenu(Player player, Groupable groupable) {

    storage.loadAllGroups().thenAccept(nodeGroups -> {

      ListMenu menu = new ListMenu(Messages.E_SUB_GROUP_TITLE.asComponent(BukkitPathFinder.getInstance().getAudiences().player(player.getUniqueId())), 4);
      menu.addPreset(MenuPresets.fillRow(new ItemStack(Material.BLACK_STAINED_GLASS_PANE),
          3)); //TODO extract icon
      for (NodeGroup group : nodeGroups) {

        TagResolver resolver = TagResolver.builder()
            .resolver(Messages.formatter().namespacedKey("key", group.getKey()))
            .resolver(Messages.formatter().number("weight", group.getWeight()))
            .resolver(Messages.formatter().modifiers("modifiers", group.getModifiers()))
            .build();

        menu.addListEntry(Button.builder()
            .withItemStack(() -> {
              ItemStack stack = new LocalizedItem.Builder(new ItemStack(
                  group.hasModifier(DiscoverableModifier.class) ? Material.CHEST_MINECART
                      : Material.FURNACE_MINECART))
                  .withName(Messages.E_SUB_GROUP_ENTRY_N.formatted(resolver))
                  .withLore(Messages.E_SUB_GROUP_ENTRY_L.formatted(resolver))
                  .createItem(player);
              if (group.contains(groupable.getNodeId())) {
                stack = ItemStackUtils.setGlow(stack);
              }
              return stack;
            })
            .withClickHandler(Action.LEFT, c -> {
              if (!group.contains(groupable.getNodeId())) {
                groupable.addGroup(group);
                storage.saveNode(groupable).thenRun(() -> {
                  c.getPlayer().playSound(c.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
                  menu.refresh(menu.getListSlots());
                });
              }
            })
            .withClickHandler(Action.RIGHT, c -> {
              if (group.contains(groupable.getNodeId())) {
                groupable.removeGroup(group.getKey());
                storage.saveNode(groupable).thenRun(() -> {
                  c.getPlayer().playSound(c.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 1f, 1f);
                  menu.refresh(menu.getListSlots());
                });
              }
            }));
      }
      menu.addPreset(presetApplier -> {
        presetApplier.addItemOnTop(3 * 9 + 8,
            new LocalizedItem(Material.BARRIER, Messages.E_SUB_GROUP_RESET_N,
                Messages.E_SUB_GROUP_RESET_L).createItem(player));
        presetApplier.addClickHandlerOnTop(3 * 9 + 8, Action.LEFT, c -> {
          groupable.clearGroups();
          storage.saveNode(groupable).thenRun(() -> {
            menu.refresh(menu.getListSlots());
            c.getPlayer().playSound(c.getPlayer().getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1f, 1f);
          });
        });

        presetApplier.addItemOnTop(3 * 9 + 4,
            new LocalizedItem(Material.PAPER, Messages.E_SUB_GROUP_INFO_N,
                Messages.E_SUB_GROUP_INFO_L).createItem(player));
      });
      menu.open(player);
    });
  }

  private void openMultiToolMenu(Player player) {
    storage.loadAllGroups().thenAccept(nodeGroups -> {

      ListMenu menu = new ListMenu(Messages.E_SUB_GROUP_TITLE.asComponent(BukkitPathFinder.getInstance().getAudiences().player(player.getUniqueId())), 4);
      menu.addPreset(MenuPresets.fillRow(new ItemStack(Material.BLACK_STAINED_GLASS_PANE),
          3)); //TODO extract icon
      for (NodeGroup group : nodeGroups) {

        TagResolver resolver = TagResolver.builder()
            .resolver(Messages.formatter().namespacedKey("key", group.getKey()))
            .resolver(Messages.formatter().number("weight", group.getWeight()))
            .resolver(Messages.formatter().modifiers("modifiers", group.getModifiers()))
            .build();

        menu.addListEntry(Button.builder()
            .withItemStack(() -> {
              ItemStack stack = new LocalizedItem.Builder(new ItemStack(
                  group.hasModifier(DiscoverableModifier.class) ? Material.CHEST_MINECART
                      : Material.FURNACE_MINECART))
                  .withName(Messages.E_SUB_GROUP_ENTRY_N.formatted(resolver))
                  .withLore(Messages.E_SUB_GROUP_ENTRY_L.formatted(resolver))
                  .createItem(player);
              if (multiTool.contains(group.getKey())) {
                stack = ItemStackUtils.setGlow(stack);
              }
              return stack;
            })
            .withClickHandler(Action.LEFT, c -> {
              if (!multiTool.contains(group.getKey())) {

                Bukkit.getScheduler().runTask(PathFinderPlugin.getInstance(), () -> {
                  multiTool.add(group.getKey());
                  c.getPlayer()
                      .playSound(c.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
                  menu.refresh(menu.getListSlots());
                });
              }
            })
            .withClickHandler(Action.RIGHT, c -> {
              if (multiTool.contains(group.getKey())) {

                Bukkit.getScheduler().runTask(PathFinderPlugin.getInstance(), () -> {
                  multiTool.remove(group.getKey());
                  c.getPlayer()
                      .playSound(c.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 1f, 1f);
                  menu.refresh(menu.getListSlots());
                });
              }
            }));
      }
      menu.addPreset(presetApplier -> {
        presetApplier.addItemOnTop(3 * 9 + 8,
            new LocalizedItem(Material.BARRIER, Messages.E_SUB_GROUP_RESET_N,
                Messages.E_SUB_GROUP_RESET_L).createItem(player));
        presetApplier.addClickHandlerOnTop(3 * 9 + 8, Action.LEFT, c -> {
          multiTool.clear();
          menu.refresh(menu.getListSlots());
          c.getPlayer()
              .playSound(c.getPlayer().getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1f,
                  1f);
        });

        presetApplier.addItemOnTop(3 * 9 + 4,
            new LocalizedItem(Material.PAPER, Messages.E_SUB_GROUP_INFO_N,
                Messages.E_SUB_GROUP_INFO_L).createItem(player));
      });
      menu.open(player);
    });
  }

  private void openNodeTypeMenu(Player player, Location location) {

    ListMenu menu = new ListMenu(Component.text("Node-Gruppen verwalten:"), 2);
    for (NodeType<?> type : types) {

      menu.addListEntry(Button.builder()
          .withItemStack(() -> new ItemStack(Material.CYAN_CONCRETE_POWDER))
          .withClickHandler(Action.RIGHT, c -> {
            storage.createAndLoadNode(type, BukkitVectorUtils.toInternal(location));
            menu.close(player);
          }));
    }
    menu.open(player);
  }
}
