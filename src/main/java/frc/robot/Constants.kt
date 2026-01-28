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
        const val DRIVER_CONTROLLER_PORT = 0
    }
}
