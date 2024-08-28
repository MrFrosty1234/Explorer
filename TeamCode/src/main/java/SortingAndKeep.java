import static java.lang.Math.abs;
import static java.lang.Math.signum;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config

public class SortingAndKeep {

    DcMotor separatorMotor;
    Servo outServo;
    DcMotor brush;

    public static double closeServoPosition = 0.94;
    public static double openServoPosition = 0.8;

    public static double oneSeparatorMove = 240;


    PID pid;

    public static double kPX = 0.001    ;
    public static double kDX = 0;
    public static double kIX = 0;

    Explorer explorer;

    public SortingAndKeep(Explorer robot) {

        explorer = robot;

        separatorMotor = explorer.linearOpMode.hardwareMap.get(DcMotor.class, "sepMotor");
        outServo = explorer.linearOpMode.hardwareMap.get(Servo.class, "outServo");
        brush = explorer.linearOpMode.hardwareMap.get(DcMotor.class, "brush");

        separatorMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        brush.setDirection(DcMotorSimple.Direction.REVERSE);
        reset();

        pid = new PID(kPX, kDX, kIX);

    }

    public void reset() {
        separatorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        separatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public boolean openServo() {
        outServo.setPosition(openServoPosition);
        return true;
    }

    public boolean closeServo() {
        outServo.setPosition(closeServoPosition);
        return true;
    }

    public boolean brushOn(){
        brush.setPower(0.9);
        return true;
    }

    public boolean bruhsOff(){
        brush.setPower(0);
        return false;
    }


    public double position = 0;
    public double time = System.currentTimeMillis() / 1000.0;
    double err = 0;
    public double t = 0;


    public boolean separatorPosition(double orientation) {
        position += oneSeparatorMove * orientation;

        err = position - separatorMotor.getCurrentPosition();

       // t = System.currentTimeMillis() / 1000.0;

       // if (abs(orientation) >= 0.1) {
            time = t;
        //}

        if (abs(err) > 5) {
            err = position - separatorMotor.getCurrentPosition();
            double power = pid.update(err);
            if (abs(power) > 0.6)
                power = 0.6 * signum(power);
            separatorMotor.setPower(power);
            return true;
        }
        separatorMotor.setPower(0);

        return false;
    }
}


