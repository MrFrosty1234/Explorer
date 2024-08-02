import static com.qualcomm.hardware.ams.AMSColorSensor.AMS_TCS34725_ADDRESS;

import static java.lang.Math.sqrt;

import com.qualcomm.hardware.adafruit.AdafruitI2cColorSensor;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDeviceWithParameters;

import java.lang.reflect.Field;

public class ColorDetective {

    AdafruitI2cColorSensor colorFieldSensor;
    AdafruitI2cColorSensor colorPuckDetectiveSensor;
    HardwareMap hardwareMap;

    public static long puckRedRed;
    public static long puckRedGreen;
    public static long puckRedBlue;
    public static long puckBlueRed;
    public static long puckBlueGreen;
    public static long puckBlueBlue;
    public static double minCosPuck = 0.95;

    public static long fieldRedRed;
    public static long fieldRedGreen;
    public static long fieldRedBlue;
    public static long fieldBlueRed;
    public static long fieldBlueGreen;
    public static long fieldBlueBlue;
    public static double minCosField = 0.95;

    public ColorDetective() {
        colorFieldSensor = fix(hardwareMap.get(AdafruitI2cColorSensor.class, "fieldSensor"));
        colorPuckDetectiveSensor = fix(hardwareMap.get(AdafruitI2cColorSensor.class, "puckSensor"));
    }

    double search(AdafruitI2cColorSensor sensor, long r1, long g1, long b1) {
        double len1, len2, len3;
        double cosA;
        len1 = sqrt(r1 ^ 2 + g1 ^ 2 + b1 ^ 2);
        len2 = sqrt(sensor.red() ^ 2 + sensor.green() ^ 2 + sensor.blue() ^ 2);
        len3 = sqrt((r1 - sensor.red()) ^ 2 + (g1 - sensor.green()) ^ 2 + (b1 - sensor.blue()) ^ 2);
        if (len1 * len2 != 0) {
            cosA = (len1 * len1 + len2 * len2 - len3 * len3) / (2 * len1 * len2);
        } else cosA = 0;
        return cosA;
    }

    //color = 0 if void, color = 1 if its blue, color = 2 if its red//

    int puckSearch() {
        double colorRed = search(colorPuckDetectiveSensor, puckRedRed, puckRedGreen, puckRedBlue);
        double colorBlue = search(colorPuckDetectiveSensor, puckBlueRed, puckBlueGreen, puckBlueBlue);
        int color = -1;
        if (colorBlue < minCosPuck && colorRed < minCosPuck)
            color = 0;
        if (colorBlue > colorRed)
            color = 1;
        if (colorRed > colorBlue)
            color = 2;
        return color;
    }

    int fieldSearch() {
        double colorRed = search(colorFieldSensor, fieldRedRed, fieldRedGreen, fieldRedBlue);
        double colorBlue = search(colorFieldSensor, fieldBlueRed, fieldBlueGreen, fieldBlueBlue);
        int color = -1;
        if (colorBlue < minCosField && colorRed < minCosField)
            color = 0;
        if (colorBlue > colorRed)
            color = 1;
        if (colorRed > colorBlue)
            color = 2;
        return color;
    }

    public static AdafruitI2cColorSensor fix(AdafruitI2cColorSensor sensor) {
        try {
            AMSColorSensor.class.getDeclaredField("AMS_TCS34725_ID").setAccessible(true);

            AMSColorSensor.Parameters parameters = new AMSColorSensor.Parameters(AMS_TCS34725_ADDRESS, 0x4D);

            Field paramField = I2cDeviceSynchDeviceWithParameters.class.getDeclaredField("parameters");

            paramField.setAccessible(true);

            try {
                paramField.set(sensor, parameters);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            sensor.initialize();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("color sensor hack not successful");
        }

        return sensor;
    }

}
