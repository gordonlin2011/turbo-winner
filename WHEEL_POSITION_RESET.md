# Wheel Position Reset System

## Overview

The robot now automatically resets all wheel positions on initialization to ensure consistent starting positions for all 4 swerve modules.

---

## What Gets Reset

### Drive Encoders
- **Action:** Set to zero (0.0)
- **Purpose:** Reset distance traveled
- **When:** Robot init and manual button press

### Turn Encoders
- **Action:** Synced to CANcoder absolute position
- **Purpose:** Ensure wheels start at correct angle
- **When:** Robot init and manual button press

---

## Automatic Reset on Init

### When It Happens
The wheel position reset is automatically called in `SwerveSubsystem` initialization:

```kotlin
init {
    // ... module creation ...

    // Reset wheel positions on init
    resetWheelPositions()
}
```

### What It Does
1. **Resets all 4 drive encoders** to zero
2. **Syncs all 4 turn encoders** to their CANcoder absolute positions
3. Ensures all modules start from a known, consistent state

### Why This Matters
- CANcoders remember absolute position even when powered off
- TalonFX internal encoders reset when power cycled
- Syncing ensures turn motors know their true wheel angle
- Prevents wheels from "snapping" to wrong position on enable

---

## Manual Reset (Button 7)

### How to Use
**Button 7** on Thrustmaster joystick - "Reset Wheel Positions"

Press Button 7 to manually reset all wheel positions during operation.

### When to Use Manual Reset
- After moving robot manually (pushing while disabled)
- If wheels seem out of sync
- After recovering from an error
- Before autonomous to ensure accurate starting position

### Feedback
Console will print: `"Wheel positions reset - all modules synced to absolute position"`

---

## Technical Implementation

### Interface Method: `SwerveModuleIO`
```kotlin
/**
 * Resets the drive encoder position to zero.
 */
fun resetDriveEncoder()

/**
 * Syncs the turn motor encoder to the absolute CANcoder position.
 * This should be called on robot init to ensure all wheels start at the correct angle.
 */
fun syncTurnEncoderToAbsolute()
```

### TalonFX Implementation
```kotlin
override fun resetDriveEncoder() {
    driveMotor.setPosition(0.0)
}

override fun syncTurnEncoderToAbsolute() {
    // Get the absolute position from the CANcoder
    val absolutePosition = cancoder.absolutePosition.refresh().valueAsDouble

    // Set the turn motor encoder to match the absolute position
    // This accounts for the gear ratio
    turnMotor.setPosition(absolutePosition * TURN_GEAR_RATIO)
}
```

### Subsystem Method
```kotlin
/**
 * Resets all wheel positions to zero and syncs turn encoders to absolute positions.
 * This should be called on robot init to ensure consistent starting positions.
 */
fun resetWheelPositions() {
    modules.forEach { it.resetEncoders() }
}
```

---

## Reset Sequence

### On Robot Power-On
1. Robot code starts
2. `SwerveSubsystem` initializes
3. All 4 modules are created
4. `resetWheelPositions()` is called automatically
5. Each module:
   - Drive encoder → 0.0
   - Turn encoder → CANcoder absolute position × gear ratio
6. Robot is ready with all wheels at known positions

### On Manual Button Press (Button 7)
1. Driver presses Button 7
2. `resetWheelPositions()` is called
3. All 4 modules reset (same as power-on)
4. Console prints confirmation message
5. Wheels are re-synced to absolute positions

---

## Advantages

### Absolute Position Tracking
- CANcoders provide absolute position (no drift)
- Survives power cycles and restarts
- Always know true wheel angle

### Consistent Behavior
- Robot behaves the same every time
- No accumulating error from relative encoders
- Predictable autonomous behavior

### Error Recovery
- Manual reset button for quick fixes
- Can recover from encoder issues
- No need to reboot robot for sync

---

## Calibration Notes

### CANcoder Offsets
The CANcoder offsets in `SwerveSubsystem.kt` define "forward":

```kotlin
val CANCODER_OFFSETS = arrayOf(
    Rotation2d.fromDegrees(0.0),   // Front Left
    Rotation2d.fromDegrees(0.0),   // Front Right
    Rotation2d.fromDegrees(0.0),   // Back Left
    Rotation2d.fromDegrees(0.0)    // Back Right
)
```

### To Calibrate Offsets:
1. Manually align all wheels straight forward
2. Open Phoenix Tuner X
3. Connect to robot
4. Note the absolute position of each CANcoder
5. Update the offsets in `SwerveSubsystem.kt`
6. Redeploy code
7. Test - wheels should now start at "forward" position

---

## Troubleshooting

### Wheels Not Aligned After Reset
**Cause:** CANcoder offsets not calibrated
**Solution:** Follow calibration procedure above

### Wheels "Snap" to Position on Enable
**Cause:** Turn encoders not synced on init
**Solution:** Should be automatic now, but can press Button 7 to manually reset

### Drive Distance Incorrect
**Cause:** Drive encoders not reset to zero
**Solution:** Check that `resetDriveEncoder()` is being called

### Console Message Not Appearing
**Cause:** Button 7 not triggering or console not visible
**Solution:**
- Check Driver Station console output
- Verify joystick button mapping in USB Devices tab
- Test button in Driver Station

---

## Code Locations

| Feature | File | Line |
|---------|------|------|
| Reset on init | `SwerveSubsystem.kt` | ~99 |
| Reset method | `SwerveSubsystem.kt` | ~176 |
| Button binding | `RobotContainer.kt` | ~69 |
| IO interface | `SwerveModuleIO.kt` | ~117-130 |
| TalonFX implementation | `SwerveModuleIOTalonFX.kt` | ~186-197 |
| Kraken implementation | `SwerveModuleIOKrakenX60.kt` | ~206-217 |

---

## Testing Checklist

### Initial Power-On Test
- [ ] Robot powers on
- [ ] No errors in console
- [ ] All 4 wheels at correct position
- [ ] Enable robot - wheels don't snap

### Manual Reset Test
- [ ] Press Button 7
- [ ] Console shows reset message
- [ ] All wheels re-sync to position
- [ ] Robot drives normally after reset

### After Manual Move Test
- [ ] Disable robot
- [ ] Manually rotate wheels by hand
- [ ] Press Button 7 (or reboot)
- [ ] Wheels return to correct position

---

## Benefits Summary

✅ **Automatic on init** - No manual intervention needed
✅ **Manual reset available** - Button 7 for quick fixes
✅ **Absolute position tracking** - Uses CANcoder for accuracy
✅ **All 4 modules synced** - Consistent behavior
✅ **Zero distance reset** - Fresh odometry each init
✅ **Error recovery** - Quick fix without reboot

---

**Implementation Complete!** ✅

All wheel positions are now automatically reset on robot initialization and can be manually reset with Button 7.

**Last Updated:** 2025-01-28
