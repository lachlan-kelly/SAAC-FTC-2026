package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "mecanumFullAuto", group = "Auto")
public class mecnumFullTest extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;

    private Servo armServo;
    private DcMotor launcherWheel;
    private DcMotor intakeMotor;

    private boolean intakeOn = false;
    private boolean leftTriggerWasPressed = false;

    private static final double triggerThreshold = 0.5;
    private static final double launcherPower = -1.0;
    private static final double intakePower = 1.0;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        armServo = hardwareMap.get(Servo.class, "armServo");
        launcherWheel = hardwareMap.get(DcMotor.class, "launcherWheel");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        launcherWheel.setDirection(DcMotor.Direction.REVERSE);
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        launcherWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        armServo.setPosition(0.0);

        waitForStart();

        if (opModeIsActive()) {
            sleep(1000);
            drive_foward(1.0);
            sleep(1000);
            stop_drive();
            sleep(1000);
            drive_back(1.0);
            sleep(1000)
            stop_drive();
        }
        private void DriveMotors() {
                private void rotate_left(double DrivePower) {
                    frontLeft.setPower(-DrivePower);
                    frontRight.setPower(DrivePower);
                    backLeft.setPower(-DrivePower);
                    backRight.setPower(DrivePower);
                }
                private void rotate_right(double DrivePower) {
                    frontLeft.setPower(DrivePower);
                    frontRight.setPower(-DrivePower);
                    backLeft.setPower(DrivePower);
                    backRight.setPower(-DrivePower);
                }
                private void stop_drive() {
                    frontLeft.setPower(0);
                    frontRight.setPower(0);
                    backLeft.setPower(0);
                    backRight.setPower(0);
                }
                private void drive_foward(double DrivePower) {
                    frontLeft.setPower(DrivePower);
                    frontRight.setPower(DrivePower);
                    backLeft.setPower(DrivePower);
                    backRight.setPower(DrivePower);
                }
                private void drive_back(double DrivePower) {
                    frontLeft.setPower(-DrivePower);
                    frontRight.setPower(-DrivePower);
                    backLeft.setPower(-DrivePower);
                    backRight.setPower(-DrivePower);
                }
                private void drive_right(double DrivePower) {
                    frontLeft.setPower(DrivePower);
                    frontRight.setPower(-DrivePower);
                    backLeft.setPower(-DrivePower);
                    backRight.setPower(DrivePower);
                }
                private void drive_left(double DrivePower) {
                    frontLeft.setPower(-DrivePower);
                    frontRight.setPower(DrivePower);
                    backLeft.setPower(DrivePower);
                    backRight.setPower(-DrivePower);
                }
            }
    }
}

