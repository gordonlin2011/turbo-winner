# Next Steps to Get Robot Running

## ✅ Completed
- [x] Code configured for TalonFX motors
- [x] Thrustmaster 16000M joystick setup
- [x] All libraries updated to 2025 versions
- [x] Build successful with correct dependencies
- [x] Phoenix 6 v25.4.0 installed
- [x] WPILib 2025.1.1 configured

## ⚠️ Required: Re-image RoboRIO

Your robot code is ready, but the **RoboRIO needs 2025 firmware**.

### Quick Steps:

1. **Open Imaging Tool**
   ```
   VS Code → Press Ctrl+Shift+P → Type "imaging"
   → Select "WPILib: Open RoboRIO Imaging Tool"
   ```

2. **Connect & Image**
   - Connect computer to RoboRIO via USB
   - Power on RoboRIO
   - In imaging tool:
     - Select your RoboRIO
     - Team: **2875**
     - Check "Format Target"
     - Click "Reformat"
   - Wait 3-5 minutes

3. **Deploy Code**
   ```bash
   cd /home/gordonli/turbo-winner
   ./gradlew deploy
   ```

4. **Test**
   - Open FRC Driver Station
   - "Robot Code" should be GREEN ✅
   - Enable teleop mode
   - Test with Thrustmaster joystick

---

## After Imaging Works

### Configure Motors (Phoenix Tuner X)
1. Open Phoenix Tuner X
2. Connect to robot
3. Verify TalonFX CAN IDs match your config:
   - Front Left: Drive=1, Turn=2, CANcoder=1
   - Front Right: Drive=3, Turn=4, CANcoder=2
   - Back Left: Drive=5, Turn=6, CANcoder=3
   - Back Right: Drive=7, Turn=8, CANcoder=4

### Calibrate CANcoders
1. Manually align all wheels straight forward
2. In Phoenix Tuner X, note the absolute position of each CANcoder
3. Update offsets in: `src/main/java/frc/robot/subsystems/SwerveSubsystem.kt:50-55`

### Test Drive
- X/Y axes: Move robot
- Twist: Rotate robot
- Button 1 (trigger): Brake mode
- Button 2 (thumb): Coast mode
- Button 3: Emergency stop

---

## Troubleshooting

See detailed guides:
- **Library errors:** `FIX_ROBORIO_ERROR.md`
- **Deployment help:** `LIBRARY_VERSIONS_2025.md`
- **All versions:** All libraries now 2025 ✅

---

## Quick Command Reference

```bash
# Build code
./gradlew build

# Deploy to robot
./gradlew deploy

# Clean build
./gradlew clean build

# Check for updates
./gradlew dependencies --refresh-dependencies
```

---

**Current Status:** Code ready ✅ | RoboRIO needs 2025 image ⚠️
