package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "mecanumFull")
public class mecnumFullTest extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;

    private Servo armServo;
    private DcMotor launcherWheel;
    private DcMotor intakeMotor;
    private CRServo grabber;

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
        grabber = hardwareMap.get(CRServo.class, "grabber");

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

        while (opModeIsActive()) {
            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double frontLeftPower = forward + strafe - turn;
            double backLeftPower = forward - strafe - turn;
            double frontRightPower = forward - strafe + turn;
            double backRightPower = forward + strafe + turn;

            double maxPower = Math.max(1.0, Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                    Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
            ));

            frontLeft.setPower(frontLeftPower / maxPower);
            backLeft.setPower(backLeftPower / maxPower);
            frontRight.setPower(frontRightPower / maxPower);
            backRight.setPower(backRightPower / maxPower);

            if (gamepad1.dpad_up) {
                armServo.setPosition(0.5);
            } else if (gamepad1.dpad_down) {
                armServo.setPosition(0.0);
            }

            launcherWheel.setPower(gamepad1.right_trigger > triggerThreshold ? launcherPower : 0.0);
            if (launcherPower > 0.0) {
                grabber.setPower(1.0);
            } else grabber.setPower(0.0);

            boolean leftTriggerPressed = gamepad1.left_trigger > triggerThreshold;
            if (leftTriggerPressed && !leftTriggerWasPressed) {
                intakeOn = !intakeOn;
            }
            leftTriggerWasPressed = leftTriggerPressed;

            intakeMotor.setPower(intakeOn ? intakePower : 0.0);
        }
    }
}

