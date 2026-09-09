package org.firstinspires.ftc.teamcode;

<<<<<<< HEAD
=======
import android.graphics.Color;
>>>>>>> ea805e9 (new auto code in testing)
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

@Autonomous(name = "DriveUntillRed_Test", group = "Auto")
public class DriveUntillRed_Test extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private NormalizedColorSensor colorSensor;

    private static final double DRIVE_POWER = 0.3;

<<<<<<< HEAD
    private static final float RED_MIN_VALUE = 0.05f;
    private static final float RED_DOMINANCE_RATIO = 1.5f;
=======
    private static final float SATURATION_GRAY_THRESHOLD = 0.35f;
    private static final float VALUE_BLACK_THRESHOLD = 0.15f;
    private static final float HUE_RED_MAX = 15f;
    private static final float HUE_RED_MIN = 345f;
>>>>>>> ea805e9 (new auto code in testing)

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }

        telemetry.addLine("Initialized. Press start.");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            driveForward();

            while (opModeIsActive() && !isRed()) {
                telemetry.addData("Status", "Driving, no red yet");
                telemetry.update();
            }

            stopMotors();

            telemetry.addData("Status", "Red detected, stopped");
            telemetry.update();
        }
    }

    private void driveForward() {
        frontLeft.setPower(DRIVE_POWER);
        frontRight.setPower(DRIVE_POWER);
        backLeft.setPower(DRIVE_POWER);
        backRight.setPower(DRIVE_POWER);
    }

    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    private boolean isRed() {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
<<<<<<< HEAD

        telemetry.addData("Raw RGB", "%.3f, %.3f, %.3f", colors.red, colors.green, colors.blue);

        if (colors.red < RED_MIN_VALUE) return false;

        return colors.red > colors.green * RED_DOMINANCE_RATIO
                && colors.red > colors.blue * RED_DOMINANCE_RATIO;
    }
}
=======
        float[] hsv = new float[3];
        Color.colorToHSV(colors.toColor(), hsv);

        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];

        telemetry.addData("Hue / Sat / Val", "%.1f / %.3f / %.3f", hue, saturation, value);

        if (value < VALUE_BLACK_THRESHOLD) return false;
        if (saturation < SATURATION_GRAY_THRESHOLD) return false;

        return hue < HUE_RED_MAX || hue >= HUE_RED_MIN;
    }
}
>>>>>>> ea805e9 (new auto code in testing)
