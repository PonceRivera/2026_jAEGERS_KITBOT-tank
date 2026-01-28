package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class MyKitBot extends SubsystemBase {
    private SparkMax m_Disparo;
    private SparkMax m_Take;
    private static final  MotorType kMotorType = MotorType.kBrushed;

    public MyKitBot() {
        m_Take = new SparkMax(6, kMotorType);
        m_Disparo = new SparkMax(5, kMotorType);
        // Default configs: set inversion via config and apply using non-deprecated configure(SparkMaxConfig)
        SparkMaxConfig takeConfig = new SparkMaxConfig();
        takeConfig.inverted(false);
        SparkMaxConfig disparoConfig = new SparkMaxConfig();
        disparoConfig.inverted(true);


    m_Disparo.configure(disparoConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_Take.configure(takeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }
    

   public Command Disparo() {
        return runOnce(() -> {
            SparkMaxConfig takeConfig = new SparkMaxConfig();
            takeConfig.inverted(false);
            m_Take.configure(takeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

            m_Disparo.set(1);
            m_Take.set(0.5);
        });
   }
    public Command Disparo2() {
    return runOnce(() -> {
        m_Disparo.set(.9);
        
    });
}
 public Command DisparoOFF() {
    return runOnce(() -> {
        m_Disparo.set(0);
        
    });
}
public Command Take() {
    return runOnce(() -> {
        m_Take.set(0.6);
        m_Disparo.set(0.6);
    });
}

//public Command Take() {
//     return runEnd(() -> m_Take.set(0.6), () -> m_Take.set(0));
//     });
// }
public Command TakeOFF() {
    return runOnce(() -> {
        m_Take.set(0);
        m_Disparo.set(0);
    });
}
public Command Take2() {
   /*
    * funcionando
    */ 
    SparkMaxConfig takeConfig = new SparkMaxConfig();
    takeConfig.inverted(true);
    m_Take.configure(takeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig disparoConfig = new SparkMaxConfig();
    disparoConfig.inverted(true);
    m_Disparo.configure(disparoConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    return runOnce(() -> {
        m_Take.set(1);
        m_Disparo.set(0.55);
    });
}
}