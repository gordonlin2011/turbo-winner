# Thrustmaster T.16000M FCS Control Scheme

## Robot Control Configuration for Team 2875

### Joystick Overview

**Model:** Thrustmaster T.16000M FCS
**Connection:** USB Port 0 on Driver Station
**Buttons:** 16 total (FRC Driver Station limit)
**Axes:** 4 (X, Y, Z/Twist, Throttle)
**Hat Switch:** 1 x 8-way POV

---

## Primary Drive Controls

### Axes Mapping

| Axis | Control | Function | Invert |
|------|---------|----------|--------|
| **X-Axis** | Left/Right Tilt | Strafe Left/Right | Yes (-) |
| **Y-Axis** | Forward/Back Tilt | Drive Forward/Backward | Yes (-) |
| **Z-Axis (Twist)** | Rotate Stick | Rotate Robot | Yes (-) |
| **Throttle** | Throttle Slider | Speed Limiter (0-100%) | No |

**Note:** All axes are inverted because joystick forward = negative, but robot forward = positive

### Drive Modes

- **Field-Relative (Default):** Robot moves relative to field, not robot orientation
- **Robot-Relative:** Robot moves relative to its own orientation

---

## Button Layout & Functions

### Stick Top Buttons

| Button | Location | Default Function | Alternative |
|--------|----------|------------------|-------------|
| **1** | Trigger | Enable Brake Mode | Fire/Action |
| **2** | Thumb Button (side) | Enable Coast Mode | Quick Turn |
| **3** | Top Left | Emergency Stop | Reset Gyro |
| **4** | Top Right | Toggle Field/Robot Relative | Auto-Align |

### Stick Base Buttons (Left Side)

| Button | Location | Default Function | Notes |
|--------|----------|------------------|-------|
| **5** | Base Row 1, Button 1 | Precision Mode (25% speed) | For fine adjustments |
| **6** | Base Row 1, Button 2 | Turbo Mode (100% speed) | Override speed limit |
| **7** | Base Row 1, Button 3 | Auto-Balance | For charging station |
| **8** | Base Row 1, Button 4 | Lock Rotation | Drive without rotation |

### Stick Base Buttons (Right Side)

| Button | Location | Default Function | Notes |
|--------|----------|------------------|-------|
| **9** | Base Row 2, Button 1 | Intake Deploy | Deploy mechanism |
| **10** | Base Row 2, Button 2 | Intake Run | Run intake motors |
| **11** | Base Row 2, Button 3 | Intake Reverse | Eject game piece |
| **12** | Base Row 2, Button 4 | Intake Retract | Stow mechanism |

### Additional Base Buttons

| Button | Location | Default Function | Notes |
|--------|----------|------------------|-------|
| **13** | Base Row 3, Button 1 | Shooter/Launcher Spin Up | Prepare to shoot |
| **14** | Base Row 3, Button 2 | Shooter/Launcher Fire | Release game piece |
| **15** | Base Row 3, Button 3 | Climber Extend | Climbing sequence |
| **16** | Base Row 3, Button 4 | Climber Retract | Lower hooks |

### POV Hat Switch (8-Way Directional)

| Direction | Angle | Function | Notes |
|-----------|-------|----------|-------|
| **Up** | 0° | Move Arm Up | Fine control |
| **Down** | 180° | Move Arm Down | Fine control |
| **Left** | 270° | Move Arm Left | Fine control |
| **Right** | 90° | Move Arm Right | Fine control |
| **Up-Right** | 45° | Preset Position: High | Quick preset |
| **Up-Left** | 315° | Preset Position: Mid | Quick preset |
| **Down-Right** | 135° | Preset Position: Low | Quick preset |
| **Down-Left** | 225° | Preset Position: Ground | Quick preset |

---

## Control Constants

### Speed Limits

| Mode | Max Speed | Max Rotation |
|------|-----------|--------------|
| **Normal** | 4.5 m/s (100%) | 2π rad/s |
| **Precision** | 1.125 m/s (25%) | π/2 rad/s |
| **Turbo** | 4.5 m/s (100%) | 2π rad/s |

### Deadbands

| Control | Deadband | Reason |
|---------|----------|--------|
| X/Y Axes | 0.1 (10%) | Prevent drift at neutral |
| Twist | 0.1 (10%) | Prevent unwanted rotation |
| Throttle | 0.05 (5%) | Finer speed control |

### Throttle Behavior

- **Position:** Down (0%) = Slow, Up (100%) = Fast
- **Range:** 0.2 to 1.0 (prevents going too slow)
- **Default:** Mid position ≈ 60% speed

---

## Button Testing in Driver Station

### How to Find Button Numbers

1. Open FRC Driver Station
2. Go to **USB Devices** tab
3. Select your Thrustmaster joystick
4. Press each button and note the number that highlights
5. Update mappings in code if needed

### Axis Testing

1. In USB Devices tab, watch axis values
2. Move joystick to verify:
   - X axis: -1 (left) to +1 (right)
   - Y axis: -1 (forward) to +1 (back)
   - Z axis: -1 (CCW) to +1 (CW)
   - Throttle: -1 (down) to +1 (up)

---

## Safety Features

### Emergency Stop (Button 3)
- **Function:** Immediately stops all motors
- **Priority:** Highest - overrides all other commands
- **Reset:** Re-enable robot to resume

### Brake Mode (Button 1)
- **Function:** Motors resist movement when stopped
- **Use Case:** Holding position on slopes
- **Trade-off:** More current draw when resisting

### Coast Mode (Button 2)
- **Function:** Motors freely spin when stopped
- **Use Case:** Pushing robot manually
- **Trade-off:** Robot won't hold position

---

## Quick Reference Card

```
╔══════════════════════════════════════════════════════╗
║   THRUSTMASTER T.16000M - TEAM 2875 CONTROLS        ║
╠══════════════════════════════════════════════════════╣
║                                                      ║
║  MOVEMENT:                                           ║
║    Tilt      → Drive Forward/Back/Strafe            ║
║    Twist     → Rotate Robot                         ║
║    Throttle  → Speed Control (0-100%)               ║
║                                                      ║
║  CRITICAL:                                           ║
║    [1] Trigger      → Brake Mode                    ║
║    [2] Thumb        → Coast Mode                    ║
║    [3] Top-Left     → EMERGENCY STOP                ║
║    [4] Top-Right    → Field/Robot Relative          ║
║                                                      ║
║  SPEED MODES:                                        ║
║    [5] Precision    → 25% Speed                     ║
║    [6] Turbo        → 100% Speed                    ║
║    [8] Lock Rotate  → Disable Rotation              ║
║                                                      ║
║  POV HAT:                                            ║
║    Directions  → Arm Manual Control                 ║
║    Diagonals   → Arm Preset Positions               ║
║                                                      ║
╚══════════════════════════════════════════════════════╝
```

---

## Code Implementation

See the following files for implementation:
- **RobotContainer.kt** - Button bindings configuration
- **DriveCommand.kt** - Main driving command with axes
- **Constants.kt** - Joystick constants and deadbands

---

## Customization Notes

### To Change Button Mappings:

1. Edit `RobotContainer.kt`
2. Find `configureBindings()` function
3. Change button numbers: `driverJoystick.button(X)`
4. Rebuild and deploy: `./gradlew deploy`

### To Adjust Sensitivity:

1. Edit `Constants.kt`
2. Modify:
   - `MAX_SPEED_METERS_PER_SECOND`
   - `MAX_ANGULAR_SPEED_RADIANS_PER_SECOND`
   - `JOYSTICK_DEADBAND`
3. Test and iterate

### To Add New Functions:

1. Create subsystem for mechanism
2. Add button binding in `RobotContainer.kt`
3. Create command for action
4. Update this documentation

---

## Driver Training

### Recommended Practice:

1. **Start in Precision Mode** (Button 5)
2. Practice basic movement with field-relative
3. Try robot-relative mode (Button 4)
4. Gradually increase throttle
5. Practice emergency stop (Button 3)
6. Master rotation control (twist)
7. Add mechanism controls as needed

### Common Mistakes:

- Forgetting to increase throttle from 0%
- Not accounting for field-relative vs robot-relative
- Overusing twist (rotation) while driving
- Forgetting brake/coast mode for different situations

---

**Last Updated:** 2025-01-28
**Robot Code Version:** 2025.1.1
**Team:** 2875
