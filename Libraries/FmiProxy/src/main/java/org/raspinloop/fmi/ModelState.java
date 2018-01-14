/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.raspinloop.fmi;

public enum ModelState implements org.apache.thrift.TEnum {
  modelStartAndEnd(1),
  modelInstantiated(2),
  modelInitializationMode(4),
  modelEventMode(8),
  modelContinuousTimeMode(16),
  modelStepComplete(35),
  modelStepInProgress(64),
  modelStepFailed(128),
  modelStepCanceled(256),
  modelTerminated(512),
  modelError(1024),
  modelFatal(2048);

  private final int value;

  private ModelState(int value) {
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
  public static ModelState findByValue(int value) { 
    switch (value) {
      case 1:
        return modelStartAndEnd;
      case 2:
        return modelInstantiated;
      case 4:
        return modelInitializationMode;
      case 8:
        return modelEventMode;
      case 16:
        return modelContinuousTimeMode;
      case 35:
        return modelStepComplete;
      case 64:
        return modelStepInProgress;
      case 128:
        return modelStepFailed;
      case 256:
        return modelStepCanceled;
      case 512:
        return modelTerminated;
      case 1024:
        return modelError;
      case 2048:
        return modelFatal;
      default:
        return null;
    }
  }
}