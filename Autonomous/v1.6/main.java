package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Program", group = "Auto")
public class Program extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        activeIntake = hardwareMap.get(DcMotor.class, "activeIntake");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        if(opModeIsActive()) {
            while(opModeIsActive()) {
            }
        }

        private void DriveMotors() {
            private void settings_drive() {
                double DrivePower = 1.0
            }
            private void stop_drive() {
                frontLeft.setPower(0);
                frontRight.setPower(0);
                backLeft.setPower(0);
                backRight.setPower(0);
            }
            private void drive_foward() {
                frontLeft.setPower(DrivePower);
                frontRight.setPower(DrivePower);
                backLeft.setPower(DrivePower);
                backRight.setPower(DrivePower);
            }
            private void drive_back() {
                frontLeft.setPower(-DrivePower);
                frontRight.setPower(-DrivePower);
                backLeft.setPower(-DrivePower);
                backRight.setPower(-DrivePower);
            }
            private void drive_right() {
                frontLeft.setPower(DrivePower);
                frontRight.setPower(-DrivePower);
                backLeft.setPower(-DrivePower);
                backRight.setPower(DrivePower);
            }
            private void drive_left() {
                frontLeft.setPower(-DrivePower);
                frontRight.setPower(DrivePower);
                backLeft.setPower(DrivePower);
                backRight.setPower(-DrivePower);
            }
        }
        private void IntakeMotors() {
            private void settings_intake() {
                double intakeSpeed = 1.0
            }
            private void stop_intake() {
                activeIntake.setPower(0)
            }
            private void IntakeClockwise() {
                activeIntake.setPower(intakeSpeed)
            }
            private void IntakeCounterclockwise() {
                activeIntake.setPower(-intakeSpeed)
            }
        }
        private void ArmMotors() {
            private void settings_arm() {
            }
            private void ArmExtend() {
            }
            private void ArmRetract() {
            }
        }
        private void LaunchMotors() {
            private void settings_launch() {
            }
            private void stop_launch() {
            }
            private void LaunchStart() {
            }
        }
    }
} 