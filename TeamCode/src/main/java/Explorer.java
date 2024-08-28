import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class Explorer {

    public DriveTrain driveTrain;
    public ColorDetective colorDetective;
    public Gyro gyro;
    public SortingAndKeep sortingAndKeep;
    public LinearOpMode linearOpMode;




    public Explorer(LinearOpMode linearOpMode1){
        linearOpMode = linearOpMode1;
        driveTrain = new DriveTrain(this);
        gyro = new Gyro(this);
        sortingAndKeep = new SortingAndKeep(this);
        colorDetective = new ColorDetective(this);


    }

    public void update(){
        gyro.update();
        driveTrain.update();

    }
}
