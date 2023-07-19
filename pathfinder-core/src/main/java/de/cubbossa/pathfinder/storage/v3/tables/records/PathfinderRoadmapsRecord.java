/*
 * This file is generated by jOOQ.
 */
package de.cubbossa.pathfinder.storage.v3.tables.records;


import de.cubbossa.pathapi.misc.NamespacedKey;
import de.cubbossa.pathfinder.storage.v3.tables.PathfinderRoadmaps;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class PathfinderRoadmapsRecord extends UpdatableRecordImpl<PathfinderRoadmapsRecord> implements Record4<NamespacedKey, String, String, Double> {

  private static final long serialVersionUID = 1L;

  /**
   * Setter for <code>pathfinder_roadmaps.key</code>.
   */
  public void setKey(NamespacedKey value) {
    set(0, value);
  }

  /**
   * Getter for <code>pathfinder_roadmaps.key</code>.
   */
  public NamespacedKey getKey() {
    return (NamespacedKey) get(0);
  }

  /**
   * Setter for <code>pathfinder_roadmaps.name_format</code>.
   */
  public void setNameFormat(String value) {
    set(1, value);
  }

  /**
   * Getter for <code>pathfinder_roadmaps.name_format</code>.
   */
  public String getNameFormat() {
    return (String) get(1);
  }

  /**
   * Setter for <code>pathfinder_roadmaps.path_visualizer</code>.
   */
  public void setPathVisualizer(String value) {
    set(2, value);
  }

  /**
   * Getter for <code>pathfinder_roadmaps.path_visualizer</code>.
   */
  public String getPathVisualizer() {
    return (String) get(2);
  }

  /**
   * Setter for <code>pathfinder_roadmaps.path_curve_length</code>.
   */
  public void setPathCurveLength(Double value) {
    set(3, value);
  }

  /**
   * Getter for <code>pathfinder_roadmaps.path_curve_length</code>.
   */
  public Double getPathCurveLength() {
    return (Double) get(3);
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------

  @Override
  public Record1<NamespacedKey> key() {
    return (Record1) super.key();
  }

  // -------------------------------------------------------------------------
  // Record4 type implementation
  // -------------------------------------------------------------------------

  @Override
  public Row4<NamespacedKey, String, String, Double> fieldsRow() {
    return (Row4) super.fieldsRow();
  }

  @Override
  public Row4<NamespacedKey, String, String, Double> valuesRow() {
    return (Row4) super.valuesRow();
  }

  @Override
  public Field<NamespacedKey> field1() {
    return PathfinderRoadmaps.PATHFINDER_ROADMAPS.KEY;
  }

  @Override
  public Field<String> field2() {
    return PathfinderRoadmaps.PATHFINDER_ROADMAPS.NAME_FORMAT;
  }

  @Override
  public Field<String> field3() {
    return PathfinderRoadmaps.PATHFINDER_ROADMAPS.PATH_VISUALIZER;
  }

  @Override
  public Field<Double> field4() {
    return PathfinderRoadmaps.PATHFINDER_ROADMAPS.PATH_CURVE_LENGTH;
  }

  @Override
  public NamespacedKey component1() {
    return getKey();
  }

  @Override
  public String component2() {
    return getNameFormat();
  }

  @Override
  public String component3() {
    return getPathVisualizer();
  }

  @Override
  public Double component4() {
    return getPathCurveLength();
  }

  @Override
  public NamespacedKey value1() {
    return getKey();
  }

  @Override
  public String value2() {
    return getNameFormat();
  }

  @Override
  public String value3() {
    return getPathVisualizer();
  }

  @Override
  public Double value4() {
    return getPathCurveLength();
  }

  @Override
  public PathfinderRoadmapsRecord value1(NamespacedKey value) {
    setKey(value);
    return this;
  }

  @Override
  public PathfinderRoadmapsRecord value2(String value) {
    setNameFormat(value);
    return this;
  }

  @Override
  public PathfinderRoadmapsRecord value3(String value) {
    setPathVisualizer(value);
    return this;
  }

  @Override
  public PathfinderRoadmapsRecord value4(Double value) {
    setPathCurveLength(value);
    return this;
  }

  @Override
  public PathfinderRoadmapsRecord values(NamespacedKey value1, String value2, String value3, Double value4) {
    value1(value1);
    value2(value2);
    value3(value3);
    value4(value4);
    return this;
  }

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  /**
   * Create a detached PathfinderRoadmapsRecord
   */
  public PathfinderRoadmapsRecord() {
    super(PathfinderRoadmaps.PATHFINDER_ROADMAPS);
  }

  /**
   * Create a detached, initialised PathfinderRoadmapsRecord
   */
  public PathfinderRoadmapsRecord(NamespacedKey key, String nameFormat, String pathVisualizer, Double pathCurveLength) {
    super(PathfinderRoadmaps.PATHFINDER_ROADMAPS);

    setKey(key);
    setNameFormat(nameFormat);
    setPathVisualizer(pathVisualizer);
    setPathCurveLength(pathCurveLength);
  }
}
