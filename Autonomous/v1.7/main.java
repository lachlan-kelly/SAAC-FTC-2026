
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        launcherWheel.setDirection(DcMotor.Direction.REVERSE);
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        launcherWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        armServo.setPosition(0.0);

        launcherWheel.setPower(0);
        intakeMotor.setPower(0);
        
        telemetry.addLine("Autonomous Ready");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            sleep(1000);
            drive_forward(0.5);
            sleep(1000);
            stop_drive();
            sleep(1000);
            drive_back(0.5);
            sleep(1000);
            stop_drive();
        }
    }
    private void rotate_left(double drivePower) {

        frontLeft.setPower(-drivePower);
        frontRight.setPower(drivePower);
        backLeft.setPower(-drivePower);
        backRight.setPower(drivePower);
    }
    private void rotate_right(double drivePower) {

        frontLeft.setPower(drivePower);
        frontRight.setPower(-drivePower);
        backLeft.setPower(drivePower);
        backRight.setPower(-drivePower);
    }
    private void stop_drive() {

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    private void drive_forward(double drivePower) {

        frontLeft.setPower(drivePower);
        frontRight.setPower(drivePower);
        backLeft.setPower(drivePower);
        backRight.setPower(drivePower);
    }
    private void drive_back(double drivePower) {

        frontLeft.setPower(-drivePower);
        frontRight.setPower(-drivePower);
        backLeft.setPower(-drivePower);
        backRight.setPower(-drivePower);
    }
    private void drive_right(double drivePower) {

        frontLeft.setPower(drivePower);
        frontRight.setPower(-drivePower);
        backLeft.setPower(-drivePower);
        backRight.setPower(drivePower);
    }
    private void drive_left(double drivePower) {

        frontLeft.setPower(-drivePower);
        frontRight.setPower(drivePower);
        backLeft.setPower(drivePower);
        backRight.setPower(-drivePower);
    }
}
