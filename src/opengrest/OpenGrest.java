package opengrest;

import alertexception.AlertException;
import controlpane.ControlPaneController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class OpenGrest extends Application {
    
    private ControlPaneController controller;
    
    @Override
    public void start(Stage stage) throws Exception {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/controlpane/ControlPane.fxml")
            );
            Parent root = (Parent) loader.load();
            controller = loader.getController();
            
            Path configFile = new File("settings.conf").toPath();
            if (Files.exists(configFile))
            {
                try
                {
                    // Leggi file di configurazione.
                    List<String> lines = Files.readAllLines(configFile);
                    int i = 0;
                    controller.mainController.lastX.set(Float.parseFloat(lines.get(i++)));
                    controller.mainController.lastY.set(Float.parseFloat(lines.get(i++)));
                    controller.mainController.lastW.set(Float.parseFloat(lines.get(i++)));
                    controller.mainController.lastH.set(Float.parseFloat(lines.get(i++)));
                    controller.mainController.isMaximized.set(Boolean.valueOf(lines.get(i++)));
                    controller.titleField.setText(lines.get(i++));
                    controller.subtitleField.setText(lines.get(i));
                    i+=3;
                    if (! lines.get(i++).equals("0"))
                    {
                        LocalDate ldt = LocalDate.of(
                            Integer.parseInt(lines.get(i - 3)),
                            Integer.parseInt(lines.get(i - 2)),
                            Integer.parseInt(lines.get(i - 1))
                        );
                        controller.datePicker.setValue(ldt);
                    }
                    if (! lines.get(i++).equals("null"))
                    {
                        controller.hourCombo.setValue(lines.get(i - 1));
                    }
                    if (! lines.get(i++).equals("null"))
                    {
                        controller.minuteCombo.setValue(lines.get(i - 1));
                    }
                    controller.targetField.setText(lines.get(i++));
                    controller.backgroundColorPicker.setValue(Color.rgb(
                            Integer.parseInt(lines.get(i++)),
                            Integer.parseInt(lines.get(i++)),
                            Integer.parseInt(lines.get(i++))
                    ));
                    controller.textArea.setText(lines.get(i++));
                    controller.fontStyleCombo.getSelectionModel().select(Integer.parseInt(lines.get(i++)));
                    controller.fontColorCombo.getSelectionModel().select(Integer.parseInt(lines.get(i++)));
                    controller.fontSizeCombo.setValue(Integer.parseInt(lines.get(i++)));
                }
                catch (Exception ex)
                {
                    AlertException.show(
                        "Errore nella lettura del file!",
                        "Valori inattesi nel file di configurazione.",
                        "Errore nella lettura della configurazione salvata (file settings.conf).\n"
                                + "Questo errore dovrebbe risolversi da solo al prossimo avvio del programma.",
                        ex
                    );
                }
            }
            
            stage = new Stage();
            stage.setTitle("OpenGrest");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnCloseRequest((e) -> handleClose());
        }
        catch (java.io.IOException ioEx)
        {
            AlertException.show(
                    "Errore nel caricamento del file!",
                    "Errore interno al programma.",
                    "Errore nel caricamenteo del file /mainpane/MainPane.fxml",
                    ioEx
            );
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void handleClose()
    {
        try (PrintWriter writer = new PrintWriter("settings.conf", "UTF-8"))
        {
            // Salva le dimensioni di MainPane.
            
            writer.println(controller.mainController.lastX.doubleValue());
            writer.println(controller.mainController.lastY.doubleValue());
            writer.println(controller.mainController.lastW.doubleValue());
            writer.println(controller.mainController.lastH.doubleValue());
            writer.println(controller.mainController.isMaximized.get());
            // Salva il contenuto di ControlPane.
            writer.println(controller.titleField.getText());
            writer.println(controller.subtitleField.getText());
            
            LocalDate ld = controller.datePicker.getValue();
            if (ld == null)
            {
                writer.println("0");
                writer.println("0");
                writer.println("0");
            }
            else
            {
                writer.println(controller.datePicker.getValue().getYear());
                writer.println(controller.datePicker.getValue().getMonthValue());
                writer.println(controller.datePicker.getValue().getDayOfMonth());
            }
            writer.println(controller.hourCombo.getValue());
            writer.println(controller.minuteCombo.getValue());
            writer.println(controller.targetField.getText());
            
            Color clr = (controller.backgroundColorPicker.getValue() != null
                    ? controller.backgroundColorPicker.getValue()
                    : Color.WHITE
            );
            writer.println((int) (clr.getRed() * 255));
            writer.println((int) (clr.getGreen() * 255));
            writer.println((int) (clr.getBlue() * 255));
            
            writer.println(controller.textArea.getText());
            writer.println(controller.fontStyleCombo.getSelectionModel().getSelectedIndex());
            writer.println(controller.fontColorCombo.getSelectionModel().getSelectedIndex());
            writer.println(controller.fontSizeCombo.getValue());
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            AlertException.show(
                "Errore nel caricamento del file!",
                "File non trovato.",
                "Errore nel caricamento del file di impostazioni settings.conf\n"
                        + "Questo errore dovrebbe risolversi da solo al prossimo avvio del programma.",
                ex
            );            
        }
        catch (UnsupportedEncodingException ex)
        {
            AlertException.show(
                "Errore nel caricamento del file!",
                "Errore di codifica.",
                "Errore nella gestione della codifica del file settings.conf\n"
                        + "Questo errore dovrebbe risolversi da solo al prossimo avvio del programma.",
                ex
            );
        }
        // Chiudi pannello principale quando viene chiuso il pannello di controllo.
        controller.mainStage.hide();
    }
    
}
