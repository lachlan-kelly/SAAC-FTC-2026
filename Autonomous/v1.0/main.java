package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "colorTest", group = "Sensor")
public class colorTest extends LinearOpMode {

    private NormalizedColorSensor colorSensor;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor activeIntake;
    
    @Override
    public void runOpMode() {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        float[] hsvValues = new float[3];

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            MOTOR_SETTINGS();
            
            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            Color.colorToHSV(colors.toColor(), hsvValues);
            float hue = hsvValues[0];

            String colorName = "Unknown";

            /**
            if (hue < 15 || hue >= 345) {
                return "Red";
            } else if (hue < 45) {
                return "Orange";
            } else if (hue < 70) {
                return "Yellow";
            } else if (hue < 170) {
                return "Green";
            } else if (hue < 200) {
                return "Cyan";
            } else if (hue < 260) {
                return "Blue";
            } else if (hue < 320) {
                return "Purple";
            } else {
                return "Pink";
            }
 */
            telemetry.addData("Color", colorName);
            telemetry.addData("Hue", hue);
            telemetry.update();

        sleep(50);
        }

        if (opModeIsActive()) {
            MECANUM_DRIVE();
        }
    }

    private void MOTOR_SETTINGS() {
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    private void MECANUM_DRIVE() {
        frontLeft.setPower(0.2);
        frontRight.setPower(0.2);
        backLeft.setPower(0.2);
        backRight.setPower(0.2);
    }
}
}