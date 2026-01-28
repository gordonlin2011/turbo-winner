package frc.robot

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.util.Units

object Constants {
    object DriveConstants {
        // Track width and wheelbase (distance between wheels)
        val TRACK_WIDTH_METERS = Units.inchesToMeters(22.0)
        val WHEELBASE_METERS = Units.inchesToMeters(22.0)

        // Swerve kinematics
        // Module positions relative to robot center (front-left, front-right, back-left, back-right)
        val SWERVE_KINEMATICS = SwerveDriveKinematics(
            Translation2d(WHEELBASE_METERS / 2.0, TRACK_WIDTH_METERS / 2.0),   // Front Left
            Translation2d(WHEELBASE_METERS / 2.0, -TRACK_WIDTH_METERS / 2.0),  // Front Right
            Translation2d(-WHEELBASE_METERS / 2.0, TRACK_WIDTH_METERS / 2.0),  // Back Left
            Translation2d(-WHEELBASE_METERS / 2.0, -TRACK_WIDTH_METERS / 2.0)  // Back Right
        )

        // Max speeds
        const val MAX_SPEED_METERS_PER_SECOND = 4.5
        const val MAX_ANGULAR_SPEED_RADIANS_PER_SECOND = Math.PI * 2.0

        // Joystick deadband
        const val JOYSTICK_DEADBAND = 0.1
    }

    object OIConstants {
        const val DRIVER_JOYSTICK_PORT = 0

        // Thrustmaster T.16000M Axis Mappings
        const val JOYSTICK_X_AXIS = 0
        const val JOYSTICK_Y_AXIS = 1
        const val JOYSTICK_Z_AXIS = 2  // Twist/Rotation
        const val JOYSTICK_THROTTLE_AXIS = 3

        // Button Mappings
        const val BUTTON_TRIGGER = 1         // Brake mode
        const val BUTTON_THUMB = 2           // Coast mode
        const val BUTTON_TOP_LEFT = 3        // Emergency stop
        const val BUTTON_TOP_RIGHT = 4       // Toggle field/robot relative

        const val BUTTON_PRECISION = 5       // 25% speed mode
        const val BUTTON_TURBO = 6           // 100% speed mode
        const val BUTTON_AUTO_BALANCE = 7    // Auto-balance
        const val BUTTON_LOCK_ROTATION = 8   // Lock rotation

        const val BUTTON_INTAKE_DEPLOY = 9   // Deploy intake
        const val BUTTON_INTAKE_RUN = 10     // Run intake
        const val BUTTON_INTAKE_REVERSE = 11 // Reverse intake
        const val BUTTON_INTAKE_RETRACT = 12 // Retract intake

        const val BUTTON_SHOOTER_SPINUP = 13 // Spin up shooter
        const val BUTTON_SHOOTER_FIRE = 14   // Fire shooter
        const val BUTTON_CLIMBER_EXTEND = 15 // Extend climber
        const val BUTTON_CLIMBER_RETRACT = 16// Retract climber

        // POV Hat Switch
        const val POV_UP = 0
        const val POV_UP_RIGHT = 45
        const val POV_RIGHT = 90
        const val POV_DOWN_RIGHT = 135
        const val POV_DOWN = 180
        const val POV_DOWN_LEFT = 225
        const val POV_LEFT = 270
        const val POV_UP_LEFT = 315

        // Deadbands
        const val AXIS_DEADBAND = 0.1
        const val TWIST_DEADBAND = 0.1
        const val THROTTLE_DEADBAND = 0.05

        // Speed modes
        const val PRECISION_MULTIPLIER = 0.25  // 25% speed
        const val NORMAL_MULTIPLIER = 1.0      // 100% speed
        const val TURBO_MULTIPLIER = 1.0       // 100% speed (can be increased if robot supports it)

        // Throttle scaling (converts -1 to 1 range to 0.2 to 1.0)
        const val THROTTLE_MIN = 0.2
        const val THROTTLE_MAX = 1.0
    }
}
