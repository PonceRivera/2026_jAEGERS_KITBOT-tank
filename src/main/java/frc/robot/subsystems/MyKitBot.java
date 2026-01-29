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
    private SparkMaxConfig disparoConfig = new SparkMaxConfig();
    private SparkMaxConfig takeConfig = new SparkMaxConfig();

    public MyKitBot() {
        m_Take = new SparkMax(6, kMotorType);
        m_Disparo = new SparkMax(5, kMotorType);
    //SparkMaxConfig disparoConfig = new SparkMaxConfig();
     //SparkMaxConfig takeConfig = new SparkMaxConfig();

        takeConfig.inverted(false);
        disparoConfig.inverted(true);

        // Do not persist these parameters to the device to avoid permanent
        // changes and cross-command interference.
    m_Take.configure(takeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_Disparo.configure(disparoConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }
    /*
     * ---------- NOTAS DE INVERSIÓN / DÓNDE CAMBIAR ----------
     *
     * 1) Inversión por comando (recomendado):
     *    - Edita SOLO el valor pasado a `set(...)` dentro del comando
     *      correspondiente para invertir su salida (usa número negativo).
     *    - Ejemplo (dentro de un comando):
     *        m_Take.set(0.6);    // no invertido
     *        m_Disparo.set(-0.6); // invertido solo para este comando
     *    - Esto NO altera la configuración del dispositivo y no afecta
     *      a otros comandos.
     *
     * 2) Inversión global (afecta a TODOS los comandos):
     *    - Edita las líneas en el constructor más arriba:
     *        takeConfig.inverted(false);
     *        disparoConfig.inverted(false);
     *      Cambia `false` a `true` para invertir globalmente.
     *    - Ten en cuenta que esto cambia la configuración del controlador
     *      y afectará a todas las llamadas `set(...)`.
     */
    

   public Command Disparo() {
        return runEnd(() -> {
            m_Disparo.set(1);
            m_Take.set(0.5);
        }, () -> {
            m_Disparo.set(0);
            m_Take.set(0);
        });
   }
//     public Command Disparo2() {
//     return runEnd(() -> {
//         m_Disparo.set(.9);
//     }, () -> {
//         m_Disparo.set(0);
//     });
// }
 public Command DisparoOFF() {
    return runEnd(() -> {
        m_Disparo.set(0);
    }, () -> {       
    });
}
/*
public Command Take() {
    return runOnce(() -> {
        m_Take.set(0.6);
        m_Disparo.set(0.6);
    });
}
*/

//public Command Take() {
//     return runEnd(() -> m_Take.set(0.6), () -> m_Take.set(0));
//     });
// }
public Command TakeOFF() {
    return runOnce(() -> {
        m_Take.set(0.6);  
        m_Disparo.set(-0.6); 
    });
}
public Command Take2() {

    return runEnd(() -> {
        m_Take.set(-0.8);
        m_Disparo.set(.55);
    }, () -> {
        m_Take.set(0);
        m_Disparo.set(0);
    });
}
 public Command TakeOUT() {
    return runOnce(() -> {
        m_Take.set(1); 
        m_Disparo.set(-0.6);
    });
}}


/*package frc.robot.subsystems;

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
    // m_Take non-inverted, m_Disparo inverted for this command only.
    return runEnd(() -> {
        m_Take.set(0.6);   // non-inverted output (same direction as default)
        m_Disparo.set(-0.6); // inverted output just for this command
    }, () -> {
        m_Take.set(0);
        m_Disparo.set(0);
    });
public Command Take2() {
   /*
    * funcionando
    */ 
    /*SparkMaxConfig takeConfig = new SparkMaxConfig();
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
} */