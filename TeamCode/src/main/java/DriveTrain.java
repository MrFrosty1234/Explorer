import android.widget.Button;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class DriveTrain {
    PID pid;

    public static double kPX = 0;
    public static double kDX = 0;
    public static double kIX = 0;
    public static double v = 0;
    DcMotor leftMotor;
    DcMotor rightMotor;
    Gyro gyro;
    HardwareMap hardwareMap;

    Button rightButton;
    Button leftButton;


    public DriveTrain(){
        pid = new PID(kPX,kDX,kIX);

        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");

        leftButton = hardwareMap.get(Button.class,"leftButton");
        rightButton = hardwareMap.get(Button.class, "rightButton");

        gyro = new Gyro();

        leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void reset(){
      leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

      rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void move(double dist){
        double err = dist;

        reset();

        double time = System.currentTimeMillis() / 1000.0;
        double tStop = 0;

        double encsnorm = (leftMotor.getCurrentPosition() + rightMotor.getCurrentPosition()) / 2.0;

        if((err < 5 && encsnorm < dist) && tStop < 5){
            encsnorm = (leftMotor.getCurrentPosition() + rightMotor.getCurrentPosition()) / 2.0;

            double t = System.currentTimeMillis() / 1000.0;
            tStop = t - time;
            err = dist - encsnorm;
            double power = pid.update(err);
            leftMotor.setPower(v - power);
            rightMotor.setPower(v + power);
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    // public void moveToWall() {
    //if(leftButton.)
    //}
}
