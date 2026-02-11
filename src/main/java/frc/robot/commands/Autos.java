package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.MyKitBot;

public final class Autos {

  /**
   * Auto simple por tiempos: retrocede y dispara.
   */
  public static Command autoTiempos(CANDriveSubsystem drive, MyKitBot kit) {
    return new SequentialCommandGroup(
        drive.driveArcade(() -> 0.3, () -> 0).withTimeout(0.25),
        drive.driveArcade(() -> 0.0, () -> 0.0).withTimeout(0.1),
        kit.DisparDelayAuto().withTimeout(5),
        kit.TakeOFF());
  }

  /**
   * Auto 1 completo por tiempos — replica la secuencia de Choreo:
   * MedioDisparo → Disparar → DisparoApelotas → Tomar → PelotasAdisparo →
   * Disparar → DisparoAneutral
   *
   * NOTA: Los tiempos y velocidades son APROXIMACIONES. Ajustar en campo.
   * Velocidades positivas = hacia adelante, negativas = hacia atrás.
   * Rotación positiva = giro a la derecha, negativa = giro a la izquierda.
   */
  public static Command auto1Tiempos(CANDriveSubsystem drive, MyKitBot kit) {
    return new SequentialCommandGroup(

        // ===== MedioDisparo (1.74s total) =====
        // Retroceder recto ~0.92m hacia la zona de disparo
        drive.driveArcade(() -> -0.35, () -> 0.0).withTimeout(1.7),
        // Frenar
        drive.driveArcade(() -> 0.0, () -> 0.0).withTimeout(0.2),

        // ===== evento "disparar" de MedioDisparo =====
        kit.DisparDelayAuto().withTimeout(5),
        kit.TakeOFF(),

        // ===== FASE 3: DisparoApelotas (7.59s total) =====
        // 1: Girar ~90° a la izquierda en el lugar (heading 180° → 90°)
        drive.driveArcade(() -> 0.0, () -> -0.45).withTimeout(1.2),
        // 2: Avanzar recto hacia las pelotas (~2m)
        drive.driveArcade(() -> 0.45, () -> 0.0).withTimeout(2.3),
        // 3: Girar ~90° más a la izquierda (heading 90° → 0°)
        drive.driveArcade(() -> 0.0, () -> -0.45).withTimeout(1.2),
        // 4: Avanzar recto hacia las pelotas MIENTRAS recoge (drive + Take2 en
        // paralelo)
        Commands.parallel(
            drive.driveArcade(() -> 0.35, () -> 0.0),
            kit.Take2()).withTimeout(3),
        kit.TakeOFF(),
        // ===== FASE 5: PelotasAdisparo (5.93s total) =====
        // Paso 1: Girar ~180° para apuntar de vuelta al disparo
        drive.driveArcade(() -> 0.0, () -> 0.45).withTimeout(2.0),
        // Paso 2: Avanzar en diagonal de regreso al punto de disparo
        drive.driveArcade(() -> 0.45, () -> -0.15).withTimeout(3.0),
        // Frenar
        drive.driveArcade(() -> 0.0, () -> 0.0).withTimeout(0.2),

        // ===== FASE 6: Disparar de nuevo (evento "disparar" de PelotasAdisparo) =====
        kit.DisparDelayAuto().withTimeout(5),
        kit.TakeOFF(),

        // ===== FASE 7: DisparoAneutral (4.97s total) =====
        // Paso 1: Girar ~90° a la izquierda para apuntar a zona neutral
        drive.driveArcade(() -> 0.0, () -> -0.45).withTimeout(1.2),
        // Paso 2: Avanzar recto hacia zona neutral (~2.55m)
        drive.driveArcade(() -> 0.45, () -> 0.0).withTimeout(3.0),
        // Paso 3: Girar ~90° para orientarse en zona neutral
        drive.driveArcade(() -> 0.0, () -> -0.45).withTimeout(1.0),
        // Frenar y terminar
        drive.driveArcade(() -> 0.0, () -> 0.0).withTimeout(0.2));
  }

  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
