/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.regis.shatu.model.TutoringSession;
import edu.regis.shatu.model.aol.AssessmentLevel;
import edu.regis.shatu.model.aol.TutoringMode;
import edu.regis.shatu.model.aol.Assessment;
import edu.regis.shatu.model.aol.StudentModel;
import edu.regis.shatu.svc.ServiceFactory;
import edu.regis.shatu.svc.StudentModelSvc;
import edu.regis.shatu.view.style.ColorScheme;

/**
 * The dashboard screen to be displayed upon user sign in. Enables user to
 * select a mode from the tutor (teach me, practice, quiz me) Tracks user's
 * progress for each mode and learning objective.
 *
 * @author Ryley MacLagan, Rickb, Cameron Brucher
 */
public class DashboardPanel extends JPanel {

    private TutoringSession model; // Reference to current tutoringSession
    private static boolean welcome = false;

    private JButton settingsButton;
    private JButton seeOneButton;
    private JButton doOneButton;
    private JButton teachOneButton;
    private JLabel welcomeLabel;

    // Panels to hold progress bars for each study mode (will be added to scroll
    // panes)
    private JPanel teachMePanel;
    private JPanel practicePanel;
    private JPanel quizMePanel;

    // Maps for individual progress bars by lesson name
    private HashMap<String, JProgressBar> teachMeProgressBars;
    private HashMap<String, JProgressBar> practiceProgressBars;
    private HashMap<String, JProgressBar> quizMeProgressBars;
    
    private JProgressBar teachMeOverallBar;
    private JProgressBar practiceOverallBar;
    private JProgressBar quizMeOverallBar;

    // List of all lesson titles from the DB (excluding IDs 0, 10, 20)
    private List<String> allLessons = new ArrayList<>();

    // Database URL (used by service/DAO layer)
    private static final String URL = "jdbc:mysql://localhost:3306/shatudb?serverTimezone=UTC";

    // Flag to track if lessons are being displayed for selection
    private boolean showingLessonSelection = false;
    
    // The tutoring mode selected by the user (for lesson selection)
    private TutoringMode selectedMode = null;

    /**
     * Creates a new DashboardPanel and initializes components.
     *
     * @param tutoringSession the current tutoring session.
     */
    public DashboardPanel() {
        initializeComponents();
        layoutComponents();
    }

    /**
     * Updates the tutoring session model.
     *
     * @param model the new tutoring session model.
     */
    public void setModel(TutoringSession model) {
        this.model = model;
        
        if (model != null) {
            switch (model.getStudent().getStudentModel().getTutoringMode()) {
                case SEE_ONE:
                    enableModeButtons(true, false, false);
                    break;
                    
                case DO_ONE:
                    enableModeButtons(false, true, false);
                    break;
                    
                case TEACH_ONE:
                    enableModeButtons(false, false, true);
            }
            
            welcomeLabel.setText("Welcome, " + model.getStudent().getAccount().getFirstName() + "!");
        }
        
        //update progress bars
        updateAllProgressBars();
    }

    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        // Header components
        settingsButton = new JButton("Settings");
        welcomeLabel = new JLabel();
        welcomeLabel.setBackground(ColorScheme.REGIS_BLUE);
        welcomeLabel.setOpaque(true);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcomeLabel.setForeground(ColorScheme.REGIS_YELLOW);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Study mode buttons
        seeOneButton = new JButton("See One");
        doOneButton = new JButton("Do One");
        teachOneButton = new JButton("Teach One");
        enableModeButtons(true, false, false);

        settingsButton.setVerticalAlignment(SwingConstants.TOP);  
        
        // Let tutor control, navigate directly to TUTOR view
        seeOneButton.addActionListener(evt -> {
            if (model != null) {
                model.getStudent().getStudentModel().setTutoringMode(TutoringMode.SEE_ONE);
            }
            MainFrame.instance().displayView(MainFrame.ViewName.TUTOR);
        });
        
        //Changed from SPLASH to TUTOR view
        // Also shows lesson selection so user can pick a lesson
        doOneButton.addActionListener(evt -> {
            selectedMode = TutoringMode.DO_ONE;
            if (model != null) {
                model.getStudent().getStudentModel().setTutoringMode(TutoringMode.DO_ONE);
            }
            showLessonSelection();
        });
        
        //Shows lesson selection with clickable lessons
        teachOneButton.addActionListener(evt -> {
            selectedMode = TutoringMode.TEACH_ONE;
            if (model != null) {
                model.getStudent().getStudentModel().setTutoringMode(TutoringMode.TEACH_ONE);
            }
            // Show lesson selection
            showLessonSelection();
        });

        // Initialize progress bar maps and panels
        teachMeProgressBars = new HashMap<>();
        practiceProgressBars = new HashMap<>();
        quizMeProgressBars = new HashMap<>();
        teachMePanel = new JPanel(new GridBagLayout());
        practicePanel = new JPanel(new GridBagLayout());
        quizMePanel = new JPanel(new GridBagLayout());
        
        if (model != null) {
    loadAllLessons();
}
    }
    
    /**
     * Shows the lesson selection panel allowing users to pick a specific lesson.
     */
    private void showLessonSelection() {
        loadAllLessons();
        
        if (allLessons.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No lessons available. Starting with default lesson.",
                "No Lessons", 
                JOptionPane.INFORMATION_MESSAGE);
            MainFrame.instance().displayView(MainFrame.ViewName.TUTOR);
            return;
        }
        
        showingLessonSelection = true;
        
        String[] lessonArray = allLessons.toArray(new String[0]);
        String selectedLesson = (String) JOptionPane.showInputDialog(
            this,
            "Select a lesson to begin:",
            "Lesson Selection",
            JOptionPane.PLAIN_MESSAGE,
            null,
            lessonArray,
            lessonArray[0]
        );
        
        if (selectedLesson != null) {
            MainFrame.instance().displayView(MainFrame.ViewName.TUTOR);
        }
        showingLessonSelection = false;
    }
    
    //Loads the lesson names from database
    private void loadAllLessons() {
        allLessons.clear(); // prevent duplicates
        try {
            if (model != null) {
                StudentModel studentModel = model.getStudent().getStudentModel();

                for (Assessment assessment : studentModel.getAssessments().values()) {
                    String lessonTitle = assessment.getOutcome().getTitle();

                    if (!allLessons.contains(lessonTitle)) {
                        allLessons.add(lessonTitle);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error Loading Lessons: " + e.getMessage());
        }
    }

    
    //Updates all progress bars when model changes
    private void updateAllProgressBars() {
        if (model == null) {
            return;
        }
        
        //update teach me progress bar
        if(teachMeProgressBars != null) {
            for (String lesson : teachMeProgressBars.keySet()) {
                
                JProgressBar bar = teachMeProgressBars.get(lesson);
                int progress = getProgressForLesson("Teach Me", lesson);
                bar.setValue(progress);
                bar.setString(progress + "%");
            }
        }
        
        //Update Practice Progress Bars
        if(practiceProgressBars != null) {
            for(String lesson : practiceProgressBars.keySet()) {
                JProgressBar bar = practiceProgressBars.get(lesson);
                int progress = getProgressForLesson("Practice", lesson);
                bar.setValue(progress);
                bar.setString(progress + "%");
            }
        }
        
        //Update Quiz Me Progress Bar
        if(quizMeProgressBars != null) {
            for(String lesson : quizMeProgressBars.keySet()) {
                JProgressBar bar = quizMeProgressBars.get(lesson);
                int progress = getProgressForLesson("Quiz Me", lesson);
                bar.setValue(progress);
                bar.setString(progress + "%");
            }
        }
        
        
        int teachMeSum = 0;
        for (String lesson : allLessons) {
            teachMeSum += getProgressForLesson("Teach Me",lesson);
        }
        int teachMeOverall = allLessons.isEmpty()? 0 : teachMeSum / allLessons.size();
        if (teachMeOverallBar != null) {
            teachMeOverallBar.setValue(teachMeOverall);
            teachMeOverallBar.setString(teachMeOverall + "%");
        }
        
        
        int practiceSum = 0;
        for (String lesson : allLessons) {
            practiceSum += getProgressForLesson("Practice",lesson);
        }
        int practiceOverall = allLessons.isEmpty()? 0 : practiceSum / allLessons.size();
        if (practiceOverallBar != null) {
            practiceOverallBar.setValue(practiceOverall);
            practiceOverallBar.setString(practiceOverall + "%");
        }
        
        int quizMeSum = 0;
        for (String lesson : allLessons) {
            quizMeSum += getProgressForLesson("Quiz Me",lesson);
        }
        int quizMeOverall = allLessons.isEmpty()? 0 : quizMeSum / allLessons.size();
        if (quizMeOverallBar != null) {
            quizMeOverallBar.setValue(quizMeOverall);
            quizMeOverallBar.setString(quizMeOverall + "%");
        }
        
        
        //repaint panel to reflect updates
        revalidate();
        repaint();
        
    }
    
    /**
     * Lays out all UI components on the panel.
     */
    private void layoutComponents() {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setPreferredSize(new Dimension(986, 480));
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.REGIS_BLUE);
        headerPanel.add(settingsButton, BorderLayout.LINE_START);
        welcomeLabel.setText("");
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(ColorScheme.REGIS_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Category panels with scroll panes
        JPanel teachMeCategoryPanel = createCategoryPanel("Teach Me");
        JScrollPane teachMeScroll = new JScrollPane(teachMeCategoryPanel);
        teachMeScroll.setPreferredSize(new Dimension(350, 300));
        gbc.gridy = 0;
        gbc.gridx = 0;
        contentPanel.add(teachMeScroll, gbc);

        JPanel practiceCategoryPanel = createCategoryPanel("Practice");
        JScrollPane practiceScroll = new JScrollPane(practiceCategoryPanel);
        practiceScroll.setPreferredSize(new Dimension(350, 300));
        gbc.gridx = 1;
        contentPanel.add(practiceScroll, gbc);

        JPanel quizMeCategoryPanel = createCategoryPanel("Quiz Me");
        JScrollPane quizMeScroll = new JScrollPane(quizMeCategoryPanel);
        quizMeScroll.setPreferredSize(new Dimension(350, 300));
        gbc.gridx = 2;
        contentPanel.add(quizMeScroll, gbc);

        // Add study mode buttons below the panels
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        contentPanel.add(seeOneButton, gbc);
        gbc.gridx = 1;
        contentPanel.add(doOneButton, gbc);
        gbc.gridx = 2;
        contentPanel.add(teachOneButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Enable/disable the three teaching mode buttons.
     * 
     * @param seeOne 
     * @param doOne
     * @param teachOne 
     */
    private void enableModeButtons(boolean seeOne, boolean doOne, boolean teachOne) {
        seeOneButton.setEnabled(seeOne);
        doOneButton.setEnabled(doOne);
        teachOneButton.setEnabled(teachOne);
    }
    

    /**
     * Shortens a lesson title to a maximum of 20 characters.
     *
     * @param lesson the original lesson title.
     * @return the shortened lesson title.
     */
    private String truncateLessonTitle(String lesson) {
        if (lesson.length() <= 23) {
            return lesson;
        }
        int lastSpace = lesson.lastIndexOf(" ", 20);
        if (lastSpace == -1) {
            return lesson.substring(0, 20) + "...";
        } else {
            return lesson.substring(0, lastSpace) + "...";
        }
    }

    /**
     * Converts an AssessmentLevel to progress percentages for [Teach Me, Practice,
     * Quiz Me].
     *
     * Mapping:
     * NOT_STARTED: [0, 0, 0]
     * VERY_LOW: [50, 0, 0]
     * LOW: [100, 0, 0]
     * MEDIUM: [100, 50, 0]
     * HIGH: [100, 100, 0]
     * VERY_HIGH: [100, 100, 50]
     * COMPLETED: [100, 100, 100]
     *
     * @param level the assessment level.
     * @return an array with progress percentages.
     */
    private int[] mapAssessmentToProgress(AssessmentLevel level) {
        switch (level) {
            case NOT_STARTED:
                return new int[] { 0, 0, 0 };
            case VERY_LOW:
                return new int[] { 50, 0, 0 };
            case LOW:
                return new int[] { 100, 0, 0 };
            case MEDIUM:
                return new int[] { 100, 50, 0 };
            case HIGH:
                return new int[] { 100, 100, 0 };
            case VERY_HIGH:
                return new int[] { 100, 100, 50 };
            case COMPLETED:
                return new int[] { 100, 100, 100 };
            default:
                return new int[] { 0, 0, 0 };
        }
    }

    /**
     * Gets the progress percentage for a lesson in a given study mode.
     *
     * @param studyMode the study mode (e.g., "Teach Me").
     * @param lesson    the lesson name.
     * @return the progress percentage.
     */
    private int getProgressForLesson(String studyMode, String lesson) {
        try {
            String userId = model.getStudent().getAccount().getUserId();
            StudentModelSvc studentModelService = ServiceFactory.findStudentModelSvc();
            AssessmentLevel level = studentModelService.retrieveAssessmentLevel(userId, lesson);
            int[] progress = mapAssessmentToProgress(level);
            if ("Teach Me".equalsIgnoreCase(studyMode)) {
                return progress[0];
            } else if ("Practice".equalsIgnoreCase(studyMode)) {
                return progress[1];
            } else if ("Quiz Me".equalsIgnoreCase(studyMode)) {
                return progress[2];
            }
        } catch (Exception e) {
            // Handle error silently
        }
        return 0;
    }

    /**
     * Creates a panel for a study mode category.
     *
     * The panel shows an overall progress bar and sections for lessons that are Not
     * Started,
     * In Progress, and Completed.
     *
     * @param category the study mode category.
     * @return the category panel.
     */
    private JPanel createCategoryPanel(String category) {
        List<String> notStarted = new ArrayList<>();
        List<String> inProgress = new ArrayList<>();
        List<String> completed = new ArrayList<>();

        for (String lesson : allLessons) {
            int progress = getProgressForLesson(category, lesson);
            if (progress == 0) {
                notStarted.add(lesson);
            } else if (progress == 50) {
                inProgress.add(lesson);
            } else if (progress == 100) {
                completed.add(lesson);
            }
        }

        if (notStarted.isEmpty()) {
            notStarted.add("None");
        }
        if (inProgress.isEmpty()) {
            inProgress.add("None");
        }
        if (completed.isEmpty()) {
            completed.add("None");
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorScheme.REGIS_YELLOW); // Yellow background
        panel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        // Category header
        JLabel header = new JLabel(category + " Progress");
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(header, gbc);

        // Overall progress bar (average progress)
        int sum = 0;
        for (String lesson : allLessons) {
            sum += getProgressForLesson(category, lesson);
        }
        int overall = allLessons.isEmpty() ? 0 : sum / allLessons.size();
        JProgressBar overallBar = new JProgressBar(0, 100);
        overallBar.setValue(overall);
        overallBar.setStringPainted(true);
        overallBar.setPreferredSize(new Dimension(70, 35));
        overallBar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        overallBar.setForeground(ColorScheme.REGIS_BLUE);
        overallBar.setBackground(Color.WHITE);
        overallBar.setString(overall + "%");
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(overallBar, gbc);

        gbc.gridwidth = 1;
        int row = 2;
        row = addSection(panel, row, "Not Started:", notStarted, "Teach Me", category);
        row = addSection(panel, row, "In Progress:", inProgress, "Practice", category);
        row = addSection(panel, row, "Completed:", completed, "Quiz Me", category);

        // Filler to push content to the top
        gbc.gridy = ++row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        JPanel filler = new JPanel();
        filler.setBackground(ColorScheme.REGIS_YELLOW);
        panel.add(filler, gbc);

        //Save Reference to Overall Bar
        if(null != category) 
                switch (category) {
                    case "Teach Me":
                        teachMeOverallBar = overallBar;
                        break;
                    case "Practice":
                        practiceOverallBar = overallBar;
                        break;
                    case "Quiz Me":
                        quizMeOverallBar = overallBar;
                        break;
                    default:
                        System.err.println("Unknown category: " + category);
                        break;
                }
        
        
        
        panel.revalidate();
        panel.repaint();
        return panel;
    }

    /**
     * Adds a section (Not Started, In Progress, or Completed) to the category
     * panel.
     *
     * For each lesson, it adds a label (with a shortened title) and a progress bar.
     * FIXED: Now includes click handlers for lesson labels to allow selection.
     *
     * @param panel            the parent panel.
     * @param startRow         the starting row index.
     * @param sectionName      the section title.
     * @param lessons          the list of lessons in this section.
     * @param expectedCategory the study mode for fetching progress.
     * @param studyMode        the current study mode.
     * @return the next row index.
     */
    private int addSection(JPanel panel, int startRow, String sectionName,
            List<String> lessons, String expectedCategory, String studyMode) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        int row = startRow;
        // Section header label
        JLabel secLabel = new JLabel(sectionName);
        secLabel.setForeground(Color.BLACK);
        secLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(secLabel, gbc);
        gbc.gridwidth = 1;

        // Add each lesson with its progress bar
        for (String lesson : lessons) {
            gbc.gridx = 0;
            gbc.gridy = row;
            String displayLesson = truncateLessonTitle(lesson);
            JLabel lessonLabel = new JLabel(displayLesson);
            lessonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lessonLabel.setForeground(Color.BLACK);
            lessonLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
            
            // Added click handler for lesson labels (not "None" placeholders)
            if (!"None".equals(lesson)) {
                final String lessonName = lesson; 
                lessonLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                lessonLabel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onLessonSelected(lessonName, studyMode);
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        lessonLabel.setForeground(ColorScheme.REGIS_BLUE);
                        lessonLabel.setText("<html><u>" + displayLesson + "</u></html>");
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        lessonLabel.setForeground(Color.BLACK);
                        lessonLabel.setText(displayLesson);
                    }
                });
            }
            
            panel.add(lessonLabel, gbc);

            if (!"None".equals(lesson)) {
                gbc.gridx = 1;
                JProgressBar bar = new JProgressBar(0, 100);
                int progress = getProgressForLesson(studyMode, lesson);
                bar.setValue(progress);
                bar.setStringPainted(true);
                bar.setPreferredSize(new Dimension(35, 25));
                bar.setFont(new Font("Segoe UI", Font.BOLD, 16));
                bar.setForeground(ColorScheme.REGIS_BLUE);
                bar.setBackground(Color.WHITE);
                bar.setString(progress + "%");
                panel.add(bar, gbc);
            
                //Store Progress Bar in the Hashmap for later updates
                if(null != studyMode) 
                switch (studyMode) {
                    case "Teach Me":
                        teachMeProgressBars.put(lesson, bar);
                        break;
                    case "Practice":
                        practiceProgressBars.put(lesson, bar);
                        break;
                    case "Quiz Me":
                        quizMeProgressBars.put(lesson, bar);
                        break;
                    default:
                        System.err.println("Unknown Study Mode: " + studyMode);
                        break;
                }
            }
            row++;
        }

        return row;
    }
    
    /**
     * Handles when a user clicks on a lesson label to select it.
     * @param lessonName the name of the selected lesson
     * @param studyMode the study mode category ("Teach Me", "Practice", "Quiz Me")
     */
    private void onLessonSelected(String lessonName, String studyMode) {
        if (model == null) {
            JOptionPane.showMessageDialog(this, 
                "No active session. Please sign in first.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        TutoringMode mode;
        switch (studyMode) {
            case "Teach Me":
                mode = TutoringMode.SEE_ONE;
                break;
            case "Practice":
                mode = TutoringMode.DO_ONE;
                break;
            case "Quiz Me":
                mode = TutoringMode.TEACH_ONE;
                break;
            default:
                mode = TutoringMode.SEE_ONE;
                break;
        }
        model.getStudent().getStudentModel().setTutoringMode(mode);

        MainFrame.instance().displayView(MainFrame.ViewName.TUTOR);
    }
}