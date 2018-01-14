/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.raspinloop.fmi.launcherRunnerIpc;

public enum StatusKind implements org.apache.thrift.TEnum {
  DoStepStatus(0),
  PendingStatus(1),
  LastSuccessfulTime(2),
  Terminated(3);

  private final int value;

  private StatusKind(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static StatusKind findByValue(int value) { 
    switch (value) {
      case 0:
        return DoStepStatus;
      case 1:
        return PendingStatus;
      case 2:
        return LastSuccessfulTime;
      case 3:
        return Terminated;
      default:
        return null;
    }
  }
}