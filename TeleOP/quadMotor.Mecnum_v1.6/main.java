package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "mecnumFullTest (use this code please)")
public class mecnumFullTest extends LinearOpMode {

  private DcMotor frontLeft;
  private DcMotor frontRight;
  private DcMotor backLeft;
  private DcMotor backRight;
  private DcMotor activeIntake;

  private enum IntakeState { OFF, FORWARD, REVERSE }
  private IntakeState intakeState = IntakeState.OFF;
  private boolean prevUpPressed = false;
  private boolean prevDownPressed = false;

  private double intakeSpeed = 0.5;
  private static final double SPEED_STEP = 0.1;
  private boolean prevLeftPressed = false;
  private boolean prevRightPressed = false;

  private NormalizedColorSensor colorSensor;
  private final float[] hsvValues = new float[3];
  private float colorGain = 30;

  @Override
  public void runOpMode() {
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    backRight = hardwareMap.get(DcMotor.class, "backRight");
    activeIntake = hardwareMap.get(DcMotor.class, "activeIntake");
    colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");

    MOTOR_SETTINGS();

    if (colorSensor instanceof SwitchableLight) {
      ((SwitchableLight) colorSensor).enableLight(true);
    }

    waitForStart();
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        MECANUM_DRIVE();
        INTAKE_MOTOR_RUN();
        COLOR_SENSOR_RUN();
        telemetry.update();
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
    boolean upPressed = gamepad1.dpad_up;
    boolean downPressed = gamepad1.dpad_down;
    boolean leftPressed = gamepad1.dpad_left;
    boolean rightPressed = gamepad1.dpad_right;

    if (upPressed && !prevUpPressed) {
      intakeState = (intakeState == IntakeState.FORWARD) ? IntakeState.OFF : IntakeState.FORWARD;
    }
    if (downPressed && !prevDownPressed) {
      intakeState = (intakeState == IntakeState.REVERSE) ? IntakeState.OFF : IntakeState.REVERSE;
    }
    prevUpPressed = upPressed;
    prevDownPressed = downPressed;

    if (intakeState != IntakeState.OFF) {
      if (leftPressed && !prevLeftPressed) {
        intakeSpeed = Math.min(1.0, intakeSpeed + SPEED_STEP);
      }
      if (rightPressed && !prevRightPressed) {
        intakeSpeed = Math.max(0.0, intakeSpeed - SPEED_STEP);
      }
    }
    prevLeftPressed = leftPressed;
    prevRightPressed = rightPressed;

    switch (intakeState) {
      case FORWARD:
        activeIntake.setPower(intakeSpeed);
        break;
      case REVERSE:
        activeIntake.setPower(-intakeSpeed);
        break;
      default:
        activeIntake.setPower(0);
        break;
    }
  }

  private void COLOR_SENSOR_RUN() {
    if (gamepad2.a) {
      colorGain += 0.005f;
    } else if (gamepad2.b && colorGain > 1) {
      colorGain -= 0.005f;
    }
    colorSensor.setGain(colorGain);

    NormalizedRGBA colors = colorSensor.getNormalizedColors();
    Color.colorToHSV(colors.toColor(), hsvValues);

    telemetry.addData("Color Gain", "%.3f", colorGain);
    telemetry.addLine()
            .addData("Red", "%.3f", colors.red)
            .addData("Green", "%.3f", colors.green)
            .addData("Blue", "%.3f", colors.blue);
    telemetry.addLine()
            .addData("Hue", "%.3f", hsvValues[0])
            .addData("Saturation", "%.3f", hsvValues[1])
            .addData("Value", "%.3f", hsvValues[2]);
    telemetry.addData("Best Guess Color", getColorName(hsvValues[0], hsvValues[1], hsvValues[2]));

    if (colorSensor instanceof DistanceSensor) {
      telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));
    }
  }

  private String getColorName(float hue, float saturation, float value) {
    if (value < 0.40) {
      return "Black";
    }
    if (saturation < 0.50) {
      return (value > 0.8) ? "White" : "Gray";
    }

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
  }
}