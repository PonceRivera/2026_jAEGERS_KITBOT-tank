// package frc.robot.subsystems;

// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;

// import com.revrobotics.spark.SparkMax;
// import com.revrobotics.spark.config.SparkMaxConfig;
// import com.revrobotics.spark.SparkBase.PersistMode;
// import com.revrobotics.spark.SparkBase.ResetMode;
// import com.revrobotics.spark.SparkLowLevel.MotorType;

// public class MyKitBot extends SubsystemBase {
//     private SparkMax m_Disparo;
//     private SparkMax m_Take;
// //      private static final int kCanID_neo6 = 6;
// //   private static final int kCanID_neo5 = 5;
//     private static final  MotorType kMotorType_neo = MotorType.kBrushless;
//     private SparkMaxConfig disparoConfig = new SparkMaxConfig();
//     private SparkMaxConfig takeConfig = new SparkMaxConfig();

//     public MyKitBot() {
//         m_Take = new SparkMax(6, kMotorType_neo);
//         m_Disparo = new SparkMax(5, kMotorType_neo);

//         takeConfig.inverted(false);
//         disparoConfig.inverted(true);

//     m_Take.configure(takeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
//     m_Disparo.configure(disparoConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
//     }
//     /*           
//      *
//      * 1) Inversión por comando (recomendado):
//      *    - Edita SOLO el valor pasado a `set(...)` dentro del comando
//      *      correspondiente para invertir su salida (usa número negativo).
//      *    - Ejemplo (dentro de un comando):
//      *        m_Take.set(0.6);    // no invertido
//      *        m_Disparo.set(-0.6); // invertido solo para este comando
//      *    - Esto NO altera la configuración del dispositivo y no afecta
//      *      a otros comandos.
//      *
//      * 2) Inversión global (afecta a TODOS los comandos):
//      *    - Edita las líneas en el constructor más arriba:
//      *        takeConfig.inverted(false);
//      *        disparoConfig.inverted(false);
//      *      Cambia `false` a `true` para invertir globalmente.
//      *    - Ten en cuenta que esto cambia la configuración del controlador
//      *      y afectará a todas las llamadas `set(...)`.
//      */

//    public Command Disparo() {
//         return runEnd(() -> {
//             m_Disparo.set(-1);
//             m_Take.set(-0.6);
//         }, () -> {
//             m_Disparo.set(0);
//             m_Take.set(0);
//         });
//    }
// //Este comando hara que el motor m_Disparo se active,
// //Tome fuerza y despues se active el motor m_Take y se mantengan a esa velocidad sin necesidad de volver a activar el m_Disparo
// // hasta que se suelte el boton, al soltarlo ambos motores se detendran.

//  public Command DisparDelay() {
//     return runEnd(() -> {
//         m_Disparo.set(-1);
//     }, () -> {
//         m_Disparo.set(0);
//      });
// }

// public Command TakeOFF() {
//     return runOnce(() -> {
//         m_Take.set(0.0);  
//         m_Disparo.set(-0.0); 
//     });
// }
// public Command Take2() {

//     return runEnd(() -> {
//         m_Take.set(.7);
//         m_Disparo.set(-0.38);
//     }, () -> {
//         m_Take.set(0);
//         m_Disparo.set(0);
//     });
// }

//  public Command TakeOUT() {
//     return runOnce(() -> {
//         m_Take.set(-0.7); 
//         m_Disparo.set(0.65);
//     });
// }}

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import edu.wpi.first.wpilibj.Timer;

public class MyKitBot extends SubsystemBase {
    private SparkMax m_Disparo;
    private SparkMax m_Take;
    // private static final int kCanID_neo6 = 6;
    // private static final int kCanID_neo5 = 5;
    private static final MotorType kMotorType_neo = MotorType.kBrushless;
    private SparkMaxConfig disparoConfig = new SparkMaxConfig();
    private SparkMaxConfig takeConfig = new SparkMaxConfig();
    private final Timer spinUpTimer = new Timer();

    public MyKitBot() {
        m_Take = new SparkMax(6, kMotorType_neo);
        m_Disparo = new SparkMax(5, kMotorType_neo);

        takeConfig.inverted(false);
        disparoConfig.inverted(true);

        m_Take.configure(takeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_Disparo.configure(disparoConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }
    /*
     *
     * 1) Inversión por comando (recomendado):
     * - Edita SOLO el valor pasado a `set(...)` dentro del comando
     * correspondiente para invertir su salida (usa número negativo).
     * - Ejemplo (dentro de un comando):
     * m_Take.set(0.6); // no invertido
     * m_Disparo.set(-0.6); // invertido solo para este comando
     * - Esto NO altera la configuración del dispositivo y no afecta
     * a otros comandos.
     *
     * 2) Inversión global (afecta a TODOS los comandos):
     * - Edita las líneas en el constructor más arriba:
     * takeConfig.inverted(false);
     * disparoConfig.inverted(false);
     * Cambia `false` a `true` para invertir globalmente.
     * - Ten en cuenta que esto cambia la configuración del controlador
     * y afectará a todas las llamadas `set(...)`.
     */

    public Command Disparo() {
        return runEnd(() -> {
            m_Disparo.set(-1);
            m_Take.set(-0.6);
        }, () -> {
            m_Disparo.set(0);
            m_Take.set(0);
        });
    }
    // Este comando hara que el motor m_Disparo se active,S
    // Tome fuerza y despues se active el motor m_Take y se mantengan a esa
    // velocidad sin necesidad de volver a activar el m_Disparo
    // hasta que se suelte el boton, al soltarlo ambos motores se detendran.

    public Command DisparDelay() {
        return runEnd(() -> {
            m_Disparo.set(-0.8);

            if (!spinUpTimer.isRunning()) {
                spinUpTimer.reset();
                spinUpTimer.start();
            }

            if (spinUpTimer.hasElapsed(1)) {
                m_Take.set(-0.75);
            }
        }, () -> {
            m_Disparo.set(0);
            m_Take.set(0);
            spinUpTimer.stop();
            spinUpTimer.reset();
        });
    }

    public Command DisparDelayAuto() {
        return runEnd(() -> {
            m_Disparo.set(-0.7);

            if (!spinUpTimer.isRunning()) {
                spinUpTimer.reset();
                spinUpTimer.start();
            }

            if (spinUpTimer.hasElapsed(1)) {
                m_Take.set(-1);
            }
        }, () -> {
            m_Disparo.set(0);
            m_Take.set(0);
            spinUpTimer.stop();
            spinUpTimer.reset();
        });
    }

    public Command TakeOFF() {
        return runOnce(() -> {
            m_Take.set(0.0);
            m_Disparo.set(-0.0);
        });
    }

    public Command Take2() {

        return runEnd(() -> {
            m_Take.set(.88);
            m_Disparo.set(-0.55);
        }, () -> {
            m_Take.set(0);
            m_Disparo.set(0);
        });
    }

    public Command TakeOUT() {
        return runOnce(() -> {
            m_Take.set(-0.7);
            m_Disparo.set(0.65);
        });
    }
}
