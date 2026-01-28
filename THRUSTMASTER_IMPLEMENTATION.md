# Thrustmaster T.16000M FCS Implementation Summary

## ✅ Complete Implementation

Your robot code now has **full Thrustmaster T.16000M FCS support** with advanced features.

---

## What Was Implemented

### 1. Control Scheme Documentation
**File:** `THRUSTMASTER_CONTROL_SCHEME.md`

Complete control layout with:
- All 16 buttons mapped
- 4 axes (X, Y, Twist, Throttle)
- 8-way POV hat switch
- Quick reference card
- Driver training guide

### 2. Enhanced Constants
**File:** `src/main/java/frc/robot/Constants.kt`

Added `OIConstants` object with:
- Button number constants (1-16)
- Axis mappings (X, Y, Z/Twist, Throttle)
- POV hat directions (0°, 45°, 90°, etc.)
- Deadband values for each axis
- Speed mode multipliers (Precision 25%, Normal 100%, Turbo 100%)
- Throttle scaling (20% min, 100% max)

### 3. Advanced Drive Command
**File:** `src/main/java/frc/robot/commands/ThrustmasterDriveCommand.kt`

Features:
- **Throttle Control:** Slider adjusts speed from 20% to 100%
- **Precision Mode:** Button 5 - 25% speed for fine adjustments
- **Turbo Mode:** Button 6 - 100% speed override
- **Field/Robot Relative:** Toggle between control modes
- **Rotation Lock:** Button 8 - Drive without rotating
- Real-time mode switching during operation

### 4. Complete Button Bindings
**File:** `src/main/java/frc/robot/RobotContainer.kt`

All 16 buttons + POV hat configured:

#### Critical Controls
- Button 1 (Trigger): Brake mode
- Button 2 (Thumb): Coast mode
- Button 3 (Top-Left): Emergency stop
- Button 4 (Top-Right): Toggle field-relative

#### Drive Modes
- Button 5: Precision mode (25% speed)
- Button 6: Turbo mode (100% speed)
- Button 7: Auto-balance (placeholder)
- Button 8: Lock rotation

#### Mechanism Placeholders
- Buttons 9-12: Intake controls (deploy, run, reverse, retract)
- Buttons 13-14: Shooter controls (spin up, fire)
- Buttons 15-16: Climber controls (extend, retract)

#### POV Hat
- Cardinal directions: Manual arm control
- Diagonal directions: Preset positions

---

## Key Features

### Throttle-Based Speed Control
```kotlin
// Throttle slider automatically scales from 20% to 100%
val throttle = MathUtil.interpolate(0.2, 1.0, (rawThrottle + 1.0) / 2.0)
```

### Speed Mode Priority
1. **Precision** (Button 5 held): 25% speed - overrides throttle
2. **Turbo** (Button 6 held): 100% speed - overrides throttle
3. **Throttle**: Variable 20-100% based on slider position

### Rotation Lock
```kotlin
// Button 8 toggles - prevents robot rotation while driving
val rotSpeed = if (rotationLocked) 0.0 else -twistInput * activeMultiplier
```

### Field-Relative Toggle
```kotlin
// Button 4 toggles between field-relative and robot-relative
fun toggleFieldRelative() {
    fieldRelative = !fieldRelative
}
```

---

## File Structure

```
src/main/java/frc/robot/
├── Constants.kt                         # Updated with OIConstants
├── RobotContainer.kt                    # All 16 buttons + POV mapped
├── commands/
│   ├── DriveCommand.kt                  # Basic drive (kept for reference)
│   └── ThrustmasterDriveCommand.kt     # Advanced Thrustmaster drive
└── subsystems/
    └── SwerveSubsystem.kt              # Swerve drive subsystem

Documentation:
├── THRUSTMASTER_CONTROL_SCHEME.md      # Complete control layout
└── THRUSTMASTER_IMPLEMENTATION.md      # This file
```

---

## How to Use

### 1. Testing Buttons in Driver Station

1. Open FRC Driver Station
2. Go to **USB Devices** tab
3. Select "Thrustmaster T.16000M FCS"
4. Press each button to see which number lights up
5. Verify button numbers match constants in code

### 2. Testing Axes

Move joystick and watch axis values:
- **Axis 0 (X):** Left/Right → Should be -1 to +1
- **Axis 1 (Y):** Forward/Back → Should be -1 to +1
- **Axis 2 (Z/Twist):** Rotate → Should be -1 to +1
- **Axis 3 (Throttle):** Slider → Should be -1 (down) to +1 (up)

### 3. Driving the Robot

**Basic Movement:**
1. Set throttle slider to mid-position (50%)
2. Tilt joystick forward/back/left/right to drive
3. Twist joystick to rotate

**Precision Mode:**
- Hold Button 5 while driving for 25% speed

**Turbo Mode:**
- Hold Button 6 while driving for 100% speed

**Rotation Lock:**
- Press Button 8 to lock rotation
- Drive without robot rotating
- Press Button 8 again to unlock

**Toggle Control Mode:**
- Press Button 4 to switch between field-relative and robot-relative

---

## Customization Guide

### To Change Button Functions

Edit `RobotContainer.kt`:
```kotlin
// Example: Change button 3 function
driverJoystick.button(OIConstants.BUTTON_TOP_LEFT).onTrue(
    swerveSubsystem.runOnce {
        // Your custom function here
    }
)
```

### To Adjust Speed Modes

Edit `Constants.kt`:
```kotlin
// In OIConstants object
const val PRECISION_MULTIPLIER = 0.25  // Change to 0.15 for even slower
const val TURBO_MULTIPLIER = 1.0       // Change to 1.5 for faster (if robot supports)
```

### To Change Deadbands

Edit `Constants.kt`:
```kotlin
// In OIConstants object
const val AXIS_DEADBAND = 0.1    // Increase if joystick drifts
const val TWIST_DEADBAND = 0.15  // Increase if unwanted rotation
```

### To Add New Subsystems

1. Create subsystem class (e.g., `IntakeSubsystem.kt`)
2. Add to `RobotContainer`:
   ```kotlin
   val intakeSubsystem = IntakeSubsystem()
   ```
3. Replace placeholder button bindings:
   ```kotlin
   driverJoystick.button(OIConstants.BUTTON_INTAKE_RUN).whileTrue(
       intakeSubsystem.runIntakeCommand()
   )
   ```

---

## Testing Checklist

Before deploying to robot:

- [ ] Build successful: `./gradlew build`
- [ ] Joystick shows up in Driver Station USB Devices tab
- [ ] All buttons respond in Driver Station
- [ ] All axes show correct values when moved
- [ ] Throttle slider moves from -1 to +1

After deploying to robot:

- [ ] Robot responds to joystick movement
- [ ] Throttle slider changes speed
- [ ] Precision mode (Button 5) slows to 25%
- [ ] Turbo mode (Button 6) increases to 100%
- [ ] Rotation lock (Button 8) prevents rotation
- [ ] Field-relative toggle (Button 4) works
- [ ] Emergency stop (Button 3) stops robot
- [ ] Brake mode (Button 1) holds position
- [ ] Coast mode (Button 2) allows free movement

---

## Common Issues & Solutions

### Joystick Not Detected
- Plug joystick into USB port 0 on Driver Station computer
- Restart Driver Station software
- Check Device Manager (Windows) or System Settings (macOS)

### Axes Inverted
- If robot moves opposite of joystick, axes are correct (code inverts them)
- If you need to change inversion, edit `ThrustmasterDriveCommand.kt`

### Button Numbers Don't Match
- Use Driver Station USB tab to verify actual button numbers
- Update `OIConstants` in `Constants.kt` if needed

### Robot Moves When Joystick Centered
- Increase `AXIS_DEADBAND` in `Constants.kt`
- Calibrate joystick in Windows/macOS settings

### Throttle Not Working
- Verify axis 3 shows values in Driver Station
- Check throttle slider moves from -1 (down) to +1 (up)
- Ensure `THROTTLE_MIN` and `THROTTLE_MAX` are correct

---

## Build Status

```
./gradlew build
BUILD SUCCESSFUL ✅
```

All code compiles and is ready for deployment!

---

## Next Steps

1. **Deploy to Robot:**
   ```bash
   ./gradlew deploy
   ```

2. **Test All Controls:**
   - Follow testing checklist above
   - Start with precision mode for safety

3. **Add Your Mechanisms:**
   - Replace placeholder TODOs with actual subsystem code
   - Test each mechanism individually

4. **Tune Performance:**
   - Adjust speed multipliers as needed
   - Fine-tune deadbands for smooth control
   - Modify throttle scaling if needed

---

**Implementation Complete!** ✅

All 16 buttons, 4 axes, and POV hat fully configured and documented.

**Last Updated:** 2025-01-28
**Build Status:** SUCCESS
**Ready for:** Deployment & Testing
