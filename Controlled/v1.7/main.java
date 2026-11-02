package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "mecnumFullTest")
public class mecnumFullTest extends LinearOpMode {

  private DcMotor frontLeft;
  private DcMotor backLeft;
  private DcMotor frontRight;
  private DcMotor backRight;

  @Override
  public void runOpMode() {
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backRight = hardwareMap.get(DcMotor.class, "backRight");

    frontLeft.setDirection(DcMotor.Direction.FORWARD);
    frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    backLeft.setDirection(DcMotor.Direction.FORWARD);
    backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    frontRight.setDirection(DcMotor.Direction.FORWARD);
    frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    backRight.setDirection(DcMotor.Direction.FORWARD);
    backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    waitForStart();
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        telemetry.update();
        frontLeft.setPower(-gamepad1.left_stick_y);
        backLeft.setPower(-gamepad1.left_stick_y);
        frontRight.setPower(-gamepad1.left_stick_y);
        backRight.setPower(-gamepad1.left_stick_y);
        frontLeft.setPower(gamepad1.left_stick_x);
        backLeft.setPower(-gamepad1.left_stick_x);
        frontRight.setPower(-gamepad1.left_stick_x);
        backRight.setPower(gamepad1.left_stick_x);
        frontLeft.setPower(-gamepad1.right_stick_x);
        backLeft.setPower(-gamepad1.right_stick_x);
        frontRight.setPower(gamepad1.right_stick_x);
        backRight.setPower(gamepad1.right_stick_x);
      }
    }
  }
}