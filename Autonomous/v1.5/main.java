package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Drives four motors forward until the colour sensor sees red tape, then stops.
 *
 * ROBOT CONFIGURATION (Driver Station):
 *   Motors on the Control/Expansion Hub named:
 *     leftFront, leftBack, rightFront, rightBack
 *   I2C device, type "REV Color/Range Sensor", named:
 *     colorSensor
 *
 * HARDWARE NOTES:
 *   - Sensor mounted 6 to 12mm above the floor, tilted 10 to 20 degrees off
 *     vertical so the LED does not reflect straight back off the glossy tape.
 *   - The V2 sensor has a physical slide switch for its white LED. Make sure
 *     it is switched ON. The software call below is a no-op on some builds.
 *
 * TUNING:
 *   Press INIT and leave it in init. Telemetry runs live before START, so you
 *   can slide the robot over the floor and then over the tape and read the
 *   actual numbers. Set RED_RATIO_MIN roughly halfway between the two.
 */
@Autonomous(name = "Drive To Red Tape", group = "Auto")
public class DriveToRedTape extends LinearOpMode {

    // ---------------- Tuning constants ----------------

    /** Drive power. Keep it slow so the sensor gets enough samples over the tape. */
    static final double DRIVE_POWER = 0.25;

    /** Sensor gain. Start at 2. Raise until alpha over the FLOOR is 0.6 to 0.7, never 1.0. */
    static final float SENSOR_GAIN = 2.0f;

    /** Red fraction R/(R+G+B) that counts as red. Floor is typically 0.33, red tape 0.5+. */
    static final double RED_RATIO_MIN = 0.45;

    /** Rejects frames with almost no signal, e.g. sensor pointing into open air. */
    static final double MIN_BRIGHTNESS = 0.010;

    /** Consecutive frames required before we believe it. Debounces noise and specular glints. */
    static final int CONFIRM_FRAMES = 3;

    /** Safety cutoff so the robot never drives off forever if it misses the tape. */
    static final double TIMEOUT_SECONDS = 6.0;

    // ---------------- Hardware ----------------

    private DcMotor leftFront, leftBack, rightFront, rightBack;
    private NormalizedColorSensor colorSensor;

    @Override
    public void runOpMode() {

        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack  = hardwareMap.get(DcMotor.class, "rightBack");

        // Right side mirrored. If the robot drives backwards, swap these two blocks.
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        leftBack.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        // Brake, not coast, so the stop is crisp and repeatable.
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
        colorSensor.setGain(SENSOR_GAIN);
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }

        // Live readings during init, for calibration.
        while (!isStarted() && !isStopRequested()) {
            showSensor("INIT - slide over floor, then tape");
        }

        if (isStopRequested()) return;

        ElapsedTime timer = new ElapsedTime();
        int redFrames = 0;
        boolean foundRed = false;

        drive(DRIVE_POWER);

        while (opModeIsActive() && timer.seconds() < TIMEOUT_SECONDS) {

            if (seesRed()) {
                redFrames++;
            } else {
                redFrames = 0;
            }

            if (redFrames >= CONFIRM_FRAMES) {
                foundRed = true;
                break;
            }

            showSensor("DRIVING - confirm " + redFrames + "/" + CONFIRM_FRAMES);
        }

        drive(0);

        telemetry.addLine(foundRed ? "STOPPED on red" : "STOPPED on timeout");
        telemetry.addData("elapsed", "%.2f s", timer.seconds());
        telemetry.update();

        // Hold the result on screen until the OpMode is stopped.
        while (opModeIsActive()) {
            idle();
        }
    }

    // ---------------- Helpers ----------------

    private boolean seesRed() {
        NormalizedRGBA c = colorSensor.getNormalizedColors();
        float sum = c.red + c.green + c.blue;

        if (sum < MIN_BRIGHTNESS) return false;

        double redRatio = c.red / sum;

        // Ratio test plus a dominance test. The second one kills off white and
        // pink surfaces that have a high red value but no real red dominance.
        return redRatio > RED_RATIO_MIN
                && c.red > c.green * 1.6f
                && c.red > c.blue * 1.6f;
    }

    private void showSensor(String state) {
        NormalizedRGBA c = colorSensor.getNormalizedColors();
        float sum = c.red + c.green + c.blue;
        double redRatio = (sum > 0) ? c.red / sum : 0;

        telemetry.addLine(state);
        telemetry.addData("red ratio", "%.3f  (threshold %.2f)", redRatio, RED_RATIO_MIN);
        telemetry.addData("R G B", "%.4f  %.4f  %.4f", c.red, c.green, c.blue);
        telemetry.addData("alpha", "%.4f  (keep under 1.0)", c.alpha);
        telemetry.addData("gain", SENSOR_GAIN);
        telemetry.update();
    }

    private void drive(double power) {
        leftFront.setPower(power);
        leftBack.setPower(power);
        rightFront.setPower(power);
        rightBack.setPower(power);
    }
}
