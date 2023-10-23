package sio.bulletin;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import sio.bulletin.Model.Etudiant;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class BulletinController implements Initializable {
    DecimalFormat df;
    HashMap<String, HashMap<String, HashMap<String, ArrayList<Etudiant>>>> lesBulletins;
    @FXML
    private AnchorPane apBulletin;
    @FXML
    private ListView lvMatieres;
    @FXML
    private ListView lvDevoirs;
    @FXML
    private ComboBox cboTrimestres;
    @FXML
    private TextField txtNomEtudiant;
    @FXML
    private TextField txtNote;
    @FXML
    private Button btnValider;
    @FXML
    private AnchorPane apMoyenne;
    @FXML
    private Button btnMenuBulletin;
    @FXML
    private Button btnMenuMoyenne;
    @FXML
    private ListView lvMatieresMoyenne;
    @FXML
    private TreeView tvMoyennesParDevoirs;
    @FXML
    private TextField txtMajor;
    @FXML
    private TextField txtNoteMaxi;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        apBulletin.toFront();
        df = new DecimalFormat("#.##");
        lesBulletins = new HashMap<>();
        lvMatieres.getItems().addAll("Maths", "Anglais", "Economie");
        lvDevoirs.getItems().addAll("Devoir n°1", "Devoir n°2", "Devoir n°3", "Devoir n°4");
        cboTrimestres.getItems().addAll("Trim 1", "Trim 2", "Trim 3");
        cboTrimestres.getSelectionModel().selectFirst();
    }

    @FXML
    public void btnMenuClicked(Event event) {
        if (event.getSource() == btnMenuBulletin) {
            apBulletin.toFront();
        } else if (event.getSource() == btnMenuMoyenne) {
            apMoyenne.toFront();
            lvMatieresMoyenne.getItems().clear();
            for (String matiere : lesBulletins.keySet()) {

                lvMatieresMoyenne.getItems().add(matiere);
            }
        }
    }

    @FXML
    public void btnValiderClicked(Event event) {
        if (lvMatieres.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Choix de la Matière");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner une matière");
            alert.showAndWait();
        } else if (lvDevoirs.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Choix du Devoir");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner un devoir");
            alert.showAndWait();

        } else if (txtNomEtudiant.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Choix de l'étudiant");
            alert.setHeaderText("");
            alert.setContentText("Saisir un étudiant");
            alert.showAndWait();
        } else if (txtNote.getText().isEmpty() || txtNote.getText().matches("[a-zA-Z]")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Choix de la note");
            alert.setHeaderText("");
            alert.setContentText("Saisir une note");
            alert.showAndWait();
        }
        // A vous de jouer
        String matiereSelectionnee = lvMatieres.getSelectionModel().getSelectedItem().toString();
        String devoirSelectionne = lvDevoirs.getSelectionModel().getSelectedItem().toString();
        String trimestreSelectionne = cboTrimestres.getSelectionModel().getSelectedItem().toString();

        if (lesBulletins.containsKey(matiereSelectionnee)) {
            HashMap<String, HashMap<String, ArrayList<Etudiant>>> devoirsExistant = lesBulletins.get(matiereSelectionnee);
            if (devoirsExistant.containsKey(devoirSelectionne)) {
                HashMap<String, ArrayList<Etudiant>> trimestresExistant = devoirsExistant.get(devoirSelectionne);
                if (trimestresExistant.containsKey(trimestreSelectionne)) {
                    trimestresExistant.get(trimestreSelectionne).add(new Etudiant(txtNomEtudiant.getText(), Double.parseDouble(txtNote.getText())));
                } else {
                    ArrayList<Etudiant> mesEtudiants = new ArrayList<>();
                    mesEtudiants.add(new Etudiant(txtNomEtudiant.getText(), Double.parseDouble(txtNote.getText())));
                    trimestresExistant.put(trimestreSelectionne, mesEtudiants);
                }
            } else {
                // Si le devoir n'existe pas, créer une nouvelle liste pour le devoir et le trimestre
                ArrayList<Etudiant> mesEtudiants = new ArrayList<>();
                mesEtudiants.add(new Etudiant(txtNomEtudiant.getText(), Double.parseDouble(txtNote.getText())));
                HashMap<String, ArrayList<Etudiant>> trimestresNouveauDevoir = new HashMap<>();
                trimestresNouveauDevoir.put(trimestreSelectionne, mesEtudiants);
                devoirsExistant.put(devoirSelectionne, trimestresNouveauDevoir);
            }
        } else {
            ArrayList<Etudiant> mesEtudiants = new ArrayList<>();
            mesEtudiants.add(new Etudiant(txtNomEtudiant.getText(), Double.parseDouble(txtNote.getText())));
            HashMap<String, ArrayList<Etudiant>> trimestresNouveauDevoir = new HashMap<>();
            trimestresNouveauDevoir.put(trimestreSelectionne, mesEtudiants);
            HashMap<String, HashMap<String, ArrayList<Etudiant>>> devoirsNouvelleMatiere = new HashMap<>();
            devoirsNouvelleMatiere.put(devoirSelectionne, trimestresNouveauDevoir);
            lesBulletins.put(matiereSelectionnee, devoirsNouvelleMatiere);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Note affectée");
        alert.setHeaderText("");
        alert.setContentText("Note enregistrée");
        alert.showAndWait();
    }
    @FXML
    public void lvMatieresMoyenneClicked(Event event) {
            TreeItem<String> racine = new TreeItem<>("Bulletins");
            tvMoyennesParDevoirs.setRoot(racine);
            for (String matiere : lesBulletins.keySet()) {
                TreeItem<String> noeudMatiere = new TreeItem<>(matiere);
                HashMap<String, HashMap<String, ArrayList<Etudiant>>> devoirs = lesBulletins.get(matiere);
                TreeItem<String> noeudDevoirs = new TreeItem<>("Par devoirs");

                for (String devoir : devoirs.keySet()) {
                    TreeItem<String> noeudDevoir = new TreeItem<>(devoir);
                    noeudDevoirs.getChildren().add(noeudDevoir);
                }
                noeudMatiere.getChildren().add(noeudDevoirs);
                racine.getChildren().add(noeudMatiere);
            }

        }

    }

}