package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

@TeleOp(name = "mecnumFullTest.Combined")
public class mecnumFullTest.Combined extends LinearOpMode {

  private DcMotor frontLeft;
  private DcMotor frontRight;
  private DcMotor backLeft;
  private DcMotor backRight;
  private DcMotor activeIntake;

  private NormalizedColorSensor colorSensor;

  private boolean activeIntakeToggle = false;
  private boolean prevIntakePressed = false;

  /**
   * This function is executed when this Op Mode is selected.
   */
  @Override
  public void runOpMode() {
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    backRight = hardwareMap.get(DcMotor.class, "backRight");
    activeIntake = hardwareMap.get(DcMotor.class, "activeIntake");

    colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");

    MOTOR_SETTINGS();
    waitForStart();
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        MECANUM_DRIVE();
        INTAKE_MOTOR_RUN();
        COLOUR_SENSOR_RUN();
      }
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
    activeIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    activeIntake.setDirection(DcMotor.Direction.REVERSE);
  }

  private void MECANUM_DRIVE() {
    float forwardBack;
    float strafe;
    float turn;
    float leftFrontPower;
    float rightFrontPower;
    float leftBackPower;
    float rightBackPower;

    forwardBack = -gamepad1.left_stick_y;
    strafe = gamepad1.left_stick_x;
    turn = gamepad1.right_stick_x;
    leftFrontPower = forwardBack + strafe + turn;
    rightFrontPower = (forwardBack - strafe) - turn;
    leftBackPower = (forwardBack - strafe) + turn;
    rightBackPower = (forwardBack + strafe) - turn;

    frontLeft.setPower(leftFrontPower);
    frontRight.setPower(rightFrontPower);
    backLeft.setPower(leftBackPower);
    backRight.setPower(rightBackPower);
  }

  private void INTAKE_MOTOR_RUN() {
    float intake = gamepad1.left_trigger;
    boolean intakePressed = intake > 0.1;

    if (intakePressed && !prevIntakePressed) {
      activeIntakeToggle = !activeIntakeToggle;
    }
    prevIntakePressed = intakePressed;

    activeIntake.setPower(activeIntakeToggle ? 1.0 : 0);
  }

  private void COLOUR_SENSOR_RUN() {
    colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");

        float[] hsvValues = new float[3];

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            Color.colorToHSV(colors.toColor(), hsvValues);
            float hue = hsvValues[0];

            String colorName = "nil";

            if (colors.alpha > 0.01) {
                if (hue >= 0 && hue < 20 || hue >= 330) {
                    colorName = "red";
                } else if (hue >= 30 && hue < 65) {
                    colorName = "orange";
                } else if (hue >= 65 && hue < 130) {
                    colorName = "yellow";
                } else if (hue >= 130 && hue < 165) {
                    colorName = "green";
                } else if (hue >= 165 && hue < 260) {
                    colorName = "blue";
                }
            } else {
                colorName = "nil";
            }

            telemetry.addData("colour", colorName);
            telemetry.addData("hue", hue);
            telemetry.update();

        sleep(50);
        }
    }
}