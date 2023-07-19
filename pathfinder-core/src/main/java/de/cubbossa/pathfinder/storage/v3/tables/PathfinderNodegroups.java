/*
 * This file is generated by jOOQ.
 */
package de.cubbossa.pathfinder.storage.v3.tables;


import de.cubbossa.pathapi.misc.NamespacedKey;
import de.cubbossa.pathfinder.storage.misc.NamespacedKeyConverter;
import de.cubbossa.pathfinder.storage.v3.DefaultSchema;
import de.cubbossa.pathfinder.storage.v3.Keys;
import de.cubbossa.pathfinder.storage.v3.tables.records.PathfinderNodegroupsRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class PathfinderNodegroups extends TableImpl<PathfinderNodegroupsRecord> {

  private static final long serialVersionUID = 1L;

  /**
   * The reference instance of <code>pathfinder_nodegroups</code>
   */
  public static final PathfinderNodegroups PATHFINDER_NODEGROUPS = new PathfinderNodegroups();

  /**
   * The class holding records for this type
   */
  @Override
  public Class<PathfinderNodegroupsRecord> getRecordType() {
    return PathfinderNodegroupsRecord.class;
  }

  /**
   * The column <code>pathfinder_nodegroups.key</code>.
   */
  public final TableField<PathfinderNodegroupsRecord, NamespacedKey> KEY = createField(DSL.name("key"), SQLDataType.VARCHAR.nullable(false), this, "", new NamespacedKeyConverter());

  /**
   * The column <code>pathfinder_nodegroups.name_format</code>.
   */
  public final TableField<PathfinderNodegroupsRecord, String> NAME_FORMAT = createField(DSL.name("name_format"), SQLDataType.VARCHAR.nullable(false), this, "");

  /**
   * The column <code>pathfinder_nodegroups.permission</code>.
   */
  public final TableField<PathfinderNodegroupsRecord, String> PERMISSION = createField(DSL.name("permission"), SQLDataType.VARCHAR, this, "");

  /**
   * The column <code>pathfinder_nodegroups.navigable</code>.
   */
  public final TableField<PathfinderNodegroupsRecord, Boolean> NAVIGABLE = createField(DSL.name("navigable"), SQLDataType.BOOLEAN.nullable(false), this, "");

  /**
   * The column <code>pathfinder_nodegroups.discoverable</code>.
   */
  public final TableField<PathfinderNodegroupsRecord, Boolean> DISCOVERABLE = createField(DSL.name("discoverable"), SQLDataType.BOOLEAN.nullable(false), this, "");

  /**
   * The column <code>pathfinder_nodegroups.find_distance</code>.
   */
  public final TableField<PathfinderNodegroupsRecord, Double> FIND_DISTANCE = createField(DSL.name("find_distance"), SQLDataType.DOUBLE.nullable(false), this, "");

  private PathfinderNodegroups(Name alias, Table<PathfinderNodegroupsRecord> aliased) {
    this(alias, aliased, null);
  }

  private PathfinderNodegroups(Name alias, Table<PathfinderNodegroupsRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /**
   * Create an aliased <code>pathfinder_nodegroups</code> table reference
   */
  public PathfinderNodegroups(String alias) {
    this(DSL.name(alias), PATHFINDER_NODEGROUPS);
  }

  /**
   * Create an aliased <code>pathfinder_nodegroups</code> table reference
   */
  public PathfinderNodegroups(Name alias) {
    this(alias, PATHFINDER_NODEGROUPS);
  }

  /**
   * Create a <code>pathfinder_nodegroups</code> table reference
   */
  public PathfinderNodegroups() {
    this(DSL.name("pathfinder_nodegroups"), null);
  }

  public <O extends Record> PathfinderNodegroups(Table<O> child, ForeignKey<O, PathfinderNodegroupsRecord> key) {
    super(child, key, PATHFINDER_NODEGROUPS);
  }

  @Override
  public Schema getSchema() {
    return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
  }

  @Override
  public UniqueKey<PathfinderNodegroupsRecord> getPrimaryKey() {
    return Keys.PATHFINDER_NODEGROUPS__PK_PATHFINDER_NODEGROUPS;
  }

  @Override
  public PathfinderNodegroups as(String alias) {
    return new PathfinderNodegroups(DSL.name(alias), this);
  }

  @Override
  public PathfinderNodegroups as(Name alias) {
    return new PathfinderNodegroups(alias, this);
  }

  @Override
  public PathfinderNodegroups as(Table<?> alias) {
    return new PathfinderNodegroups(alias.getQualifiedName(), this);
  }

  /**
   * Rename this table
   */
  @Override
  public PathfinderNodegroups rename(String name) {
    return new PathfinderNodegroups(DSL.name(name), null);
  }

  /**
   * Rename this table
   */
  @Override
  public PathfinderNodegroups rename(Name name) {
    return new PathfinderNodegroups(name, null);
  }

  /**
   * Rename this table
   */
  @Override
  public PathfinderNodegroups rename(Table<?> name) {
    return new PathfinderNodegroups(name.getQualifiedName(), null);
  }

  // -------------------------------------------------------------------------
  // Row6 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row6<NamespacedKey, String, String, Boolean, Boolean, Double> fieldsRow() {
    return (Row6) super.fieldsRow();
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
   */
  public <U> SelectField<U> mapping(Function6<? super NamespacedKey, ? super String, ? super String, ? super Boolean, ? super Boolean, ? super Double, ? extends U> from) {
    return convertFrom(Records.mapping(from));
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Class,
   * Function)}.
   */
  public <U> SelectField<U> mapping(Class<U> toType, Function6<? super NamespacedKey, ? super String, ? super String, ? super Boolean, ? super Boolean, ? super Double, ? extends U> from) {
    return convertFrom(toType, Records.mapping(from));
  }
}
