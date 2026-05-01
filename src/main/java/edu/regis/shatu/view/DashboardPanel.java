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

import edu.regis.shatu.err.NonRecoverableException;
import edu.regis.shatu.err.ObjNotFoundException;
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


import edu.regis.shatu.dao.DeveloperModeDAO;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;


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

import com.google.gson.Gson;

import edu.regis.shatu.model.Account;
import edu.regis.shatu.model.aol.NewExampleRequest;
import edu.regis.shatu.model.aol.PendingTask;
import edu.regis.shatu.model.aol.ProblemType;
import edu.regis.shatu.svc.ClientRequest;
import edu.regis.shatu.svc.ServerRequestType;
import edu.regis.shatu.svc.SvcFacade;
import edu.regis.shatu.svc.TutorReply;

/**
 * The dashboard screen to be displayed upon user sign in. Enables user to
 * select a mode from the tutor (teach me, practice, quiz me) Tracks user's
 * progress for each mode and learning objective.
 *
 * @author Ryley MacLagan, Rickb, Cameron Brucher
 */
public class DashboardPanel extends JPanel {

    private TutoringSession model; // Reference to current tutoringSession
    private static final boolean welcome = false;

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
    
    
    
    
    private int getVisibleModeCount() {
        if (model == null || model.getStudent() == null || model.getStudent().getStudentModel() == null) {
            return 3;
        }

        TutoringMode dashboardMode = model.getStudent().getStudentModel().getTutoringMode();

        return switch (dashboardMode) {
            case SEE_ONE -> 1;
            case DO_ONE -> 2;
            case TEACH_ONE -> 3;
            default -> 3;
        };
    }




    /**
     * Updates the tutoring session model.
     *
     * @param model the new tutoring session model.
     */
    public void setModel(TutoringSession model) {
        this.model = model;

        if (model != null) {
            // TutoringMode dashboardMode = TutoringMode.DO_ONE;
            // TutoringMode dashboardMode = TutoringMode.TEACH_ONE;

            TutoringMode dashboardMode = model.getStudent().getStudentModel().getTutoringMode();

            System.out.println("[DashboardPanel.setModel] dashboardMode=" + dashboardMode);

            switch (dashboardMode) {
                case SEE_ONE -> enableModeButtons(true, false, false);

                case DO_ONE -> enableModeButtons(true, true, false);

                case TEACH_ONE -> enableModeButtons(true, true, true);

                default -> enableModeButtons(true, false, false);

            }

            welcomeLabel.setText("Welcome, " + model.getStudent().getAccount().getFirstName() + "!");
            loadAllLessons();
            rebuildDashboard();

        }

        //update progress bars
        updateAllProgressBars();

    }

    /**
     * Ensure allLessons populates and recalculate progress bars from lessons
     */
    private void rebuildDashboard() {
        removeAll();

        layoutComponents();

        revalidate();

        repaint();

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

        settingsButton.addActionListener(evt -> showDeveloperModeSettings());
        
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
     * Shows the lesson selection panel allowing users to pick a specific
     * lesson.
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
        } catch (Exception e) { // ToDo: Catch specific exception.
            System.err.println("Error Loading Lessons: " + e.getMessage());
        }

    }

    //Updates all progress bars when model changes
    private void updateAllProgressBars() {
        if (model == null) {
            return;
        }

        //update teach me progress bar
        if (teachMeProgressBars != null) {
            for (String lesson : teachMeProgressBars.keySet()) {

                JProgressBar bar = teachMeProgressBars.get(lesson);
                int progress = getProgressForLesson("Teach Me", lesson);
                bar.setValue(progress);
                bar.setString(progress + "%");
            }
        }

        //Update Practice Progress Bars
        if (practiceProgressBars != null) {
            for (String lesson : practiceProgressBars.keySet()) {
                JProgressBar bar = practiceProgressBars.get(lesson);
                int progress = getProgressForLesson("Practice", lesson);
                bar.setValue(progress);
                bar.setString(progress + "%");
            }
        }

        //Update Quiz Me Progress Bar
        if (quizMeProgressBars != null) {
            for (String lesson : quizMeProgressBars.keySet()) {
                JProgressBar bar = quizMeProgressBars.get(lesson);
                int progress = getProgressForLesson("Quiz Me", lesson);
                bar.setValue(progress);
                bar.setString(progress + "%");
            }
        }

        int teachMeSum = 0;
        for (String lesson : allLessons) {
            teachMeSum += getProgressForLesson("Teach Me", lesson);
        }
        int teachMeOverall = allLessons.isEmpty() ? 0 : teachMeSum / allLessons.size();
        if (teachMeOverallBar != null) {
            teachMeOverallBar.setValue(teachMeOverall);
            teachMeOverallBar.setString(teachMeOverall + "%");
        }

        int practiceSum = 0;
        for (String lesson : allLessons) {
            practiceSum += getProgressForLesson("Practice", lesson);
        }

        int practiceOverall = allLessons.isEmpty() ? 0 : practiceSum / allLessons.size();
        if (practiceOverallBar != null) {
            practiceOverallBar.setValue(practiceOverall);
            practiceOverallBar.setString(practiceOverall + "%");
        }

        int quizMeSum = 0;
        for (String lesson : allLessons) {
            quizMeSum += getProgressForLesson("Quiz Me", lesson);
        }

        int quizMeOverall = allLessons.isEmpty() ? 0 : quizMeSum / allLessons.size();
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

        int visibleModeCount = getVisibleModeCount();

        // first column: always show see one / teach me
        JPanel teachMeCategoryPanel = createCategoryPanel("Teach Me");
        JScrollPane teachMeScroll = new JScrollPane(teachMeCategoryPanel);
        teachMeScroll.setPreferredSize(new Dimension(350, 300));
        gbc.gridy = 0;
        gbc.gridx = 0;
        contentPanel.add(teachMeScroll, gbc);

        // second column: only show for do one and above
        if (visibleModeCount >= 2) {
            JPanel practiceCategoryPanel = createCategoryPanel("Practice");
            JScrollPane practiceScroll = new JScrollPane(practiceCategoryPanel);
            practiceScroll.setPreferredSize(new Dimension(350, 300));
            gbc.gridy = 0;
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            contentPanel.add(practiceScroll, gbc);
        }

        // third column: only show for teach one
        if (visibleModeCount >= 3) {
            JPanel quizMeCategoryPanel = createCategoryPanel("Quiz Me");
            JScrollPane quizMeScroll = new JScrollPane(quizMeCategoryPanel);
            quizMeScroll.setPreferredSize(new Dimension(350, 300));
            gbc.gridy = 0;
            gbc.gridx = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            contentPanel.add(quizMeScroll, gbc);
        }

        // always show all three mode buttons
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
     * Converts an AssessmentLevel to progress percentages for [Teach Me,
     * Practice, Quiz Me].
     *
     * Mapping: NOT_STARTED: [0, 0, 0] VERY_LOW: [50, 0, 0] LOW: [100, 0, 0]
     * MEDIUM: [100, 50, 0] HIGH: [100, 100, 0] VERY_HIGH: [100, 100, 50]
     * COMPLETED: [100, 100, 100]
     *
     * @param level the assessment level.
     * @return an array with progress percentages.
     */
    private int[] mapAssessmentToProgress(AssessmentLevel level) {
        return switch (level) {
            case NOT_STARTED -> new int[] { 0, 0, 0 };
            case VERY_LOW -> new int[] { 50, 0, 0 };
            case LOW -> new int[] { 100, 0, 0 };
            case MEDIUM -> new int[] { 100, 50, 0 };
            case HIGH -> new int[] { 100, 100, 0 };
            case VERY_HIGH -> new int[] { 100, 100, 50 };
            case COMPLETED -> new int[] { 100, 100, 100 };
            default -> new int[] { 0, 0, 0 };
        };

    }

    /**
     * Gets the progress percentage for a lesson in a given study mode.
     *
     * @param studyMode the study mode (e.g., "Teach Me").
     * @param lesson the lesson name.
     * @return the progress percentage.
     */
    private int getProgressForLesson(String studyMode, String lesson) {
        // ToDo: Why is this try block here, the code should never fail!
        try {
            String userId = model.getStudent().getAccount().getUserId();
            StudentModelSvc studentModelService = ServiceFactory.findStudentModelSvc();
            // System.out.println("Dashboard requesting progress for studyMode = " + studyMode
            //   + ", lesson = [" + lesson + "]");
            AssessmentLevel level = studentModelService.retrieveAssessmentLevel(userId, lesson);
            // System.out.println("Dashboard requesting assessment level for userId = "
            //     + userId + ", lesson = [" + lesson + "]");
            int[] progress = mapAssessmentToProgress(level);
            if ("Teach Me".equalsIgnoreCase(studyMode)) {
                return progress[0];
            } else if ("Practice".equalsIgnoreCase(studyMode)) {
                return progress[1];
            } else if ("Quiz Me".equalsIgnoreCase(studyMode)) {
                return progress[2];
            }
        } catch (NonRecoverableException | ObjNotFoundException e) {
            // ToDo: Not sure this is correct, but if it is, it should not
            // be handling Exception, instead a subclass.
            // Handle error silently
        }
        return 0;
    }

    /**
     * Creates a panel for a study mode category.
     *
     * The panel shows an overall progress bar and sections for lessons that are
     * Not Started, In Progress, and Completed.
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
        row = addSection(panel, row, "Not Started:", notStarted, category);
        row = addSection(panel, row, "In Progress:", inProgress, category);
        row = addSection(panel, row, "Completed:", completed, category);

        // Filler to push content to the top
        gbc.gridy = ++row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        JPanel filler = new JPanel();
        filler.setBackground(ColorScheme.REGIS_YELLOW);
        panel.add(filler, gbc);

        //Save Reference to Overall Bar
        if (null != category) {
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
        }

        panel.revalidate();
        panel.repaint();
        return panel;
    }

    /**
     * Adds a section (Not Started, In Progress, or Completed) to the category
     * panel.
     *
     * For each lesson, it adds a label (with a shortened title) and a progress
     * bar. FIXED: Now includes click handlers for lesson labels to allow
     * selection.
     *
     * @param panel the parent panel.
     * @param startRow the starting row index.
     * @param sectionName the section title.
     * @param lessons the list of lessons in this section.
     * @param expectedCategory the study mode for fetching progress.
     * @param studyMode the current study mode.
     * @return the next row index.
     */
    private int addSection(JPanel panel, int startRow, String sectionName,
            List<String> lessons, String studyMode) {
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
                if (null != studyMode) {
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
            }
            row++;
        }

        return row;
    }

    private ProblemType getProblemTypeForLesson(String lessonName) {
        if (lessonName == null) {
            return null;
        }

        String lesson = lessonName.trim().toLowerCase();

        if (lesson.contains("encode ascii") || lesson.contains("encode hex") || lesson.contains("binary hex")) {
            return ProblemType.ASCII_ENCODE;
        }
        if (lesson.contains("add one bit")) {
            return ProblemType.ADD_ONE_BIT;
        }
        if (lesson.contains("pad with zero")) {
            return ProblemType.PAD_ZEROS;
        }
        if (lesson.contains("msg length")) {
            return ProblemType.ADD_MSG_LENGTH;
        }
        if (lesson.contains("prepare schedule")) {
            return ProblemType.PREPARE_SCHEDULE;
        }
        if (lesson.contains("initialize variables")) {
            return ProblemType.INITIALIZE_VARS;
        }
        if (lesson.contains("compression round")) {
            return ProblemType.COMPRESS_ROUND;
        }
        if (lesson.contains("rotate bits")) {
            return ProblemType.ROTATE_BITS;
        }
        if (lesson.contains("shift bits")) {
            return ProblemType.SHIFT_BITS;
        }
        if (lesson.contains("xor bits")) {
            return ProblemType.XOR_BITS;
        }
        if (lesson.contains("add bit strings")) {
            return ProblemType.ADD_BITS;
        }
        if (lesson.contains("majority function")) {
            return ProblemType.MAJORITY_FUNCTION;
        }
        if (lesson.contains("choice function")) {
            return ProblemType.CHOICE_FUNCTION;
        }
        if (lesson.contains("sha sum 0")) {
            return ProblemType.SHA_ZERO;
        }
        if (lesson.contains("sha sum 1")) {
            return ProblemType.SHA_ONE;
        }

        return null;
    }

    private StepSelection getStepSelectionForLesson(String lessonName) {
        if (lessonName == null) {
            return null;
        }

        String lesson = lessonName.trim().toLowerCase();

        if (lesson.contains("information message")) {
            return StepSelection.OVERVIEW;
        }
        if (lesson.contains("encode ascii") || lesson.contains("encode hex") || lesson.contains("binary hex")) {
            return StepSelection.ENCODE;
        }
        if (lesson.contains("add one bit")) {
            return StepSelection.ADD1;
        }
        if (lesson.contains("pad with zero")) {
            return StepSelection.PAD;
        }
        if (lesson.contains("msg length")) {
            return StepSelection.LENGTH;
        }
        if (lesson.contains("prepare schedule")) {
            return StepSelection.PREPARE;
        }
        if (lesson.contains("initialize variables")) {
            return StepSelection.INIT_VARS;
        }
        if (lesson.contains("compression round")) {
            return StepSelection.COMPRESS;
        }
        if (lesson.contains("rotate bits")) {
            return StepSelection.ROTATE_BITS;
        }
        if (lesson.contains("shift bits")) {
            return StepSelection.SHIFT_RIGHT;
        }
        if (lesson.contains("xor bits")) {
            return StepSelection.XOR;
        }
        if (lesson.contains("add bit strings")) {
            return StepSelection.ADD_TWO_BIT;
        }
        if (lesson.contains("majority function")) {
            return StepSelection.MAJ_FUNCTION;
        }
        if (lesson.contains("choice function")) {
            return StepSelection.CHOICE_FUNCTION;
        }
        if (lesson.contains("sha sum 0")) {
            return StepSelection.SHA_ZERO;
        }
        if (lesson.contains("sha sum 1")) {
            return StepSelection.SHA_ONE;
        }

        return null;
    }

    private void loadRequestedLesson(String lessonName) {
        try {
            ProblemType problemType = getProblemTypeForLesson(lessonName);

            if (problemType == null) {
                return;
            }

            TutoringSession session = MainFrame.instance().getModel();
            if (session == null) {
                return;
            }

            Account account = session.getStudent().getAccount();
            if (account == null) {
                return;
            }

            Gson gson = new Gson();

            NewExampleRequest ex = new NewExampleRequest();
            ex.setExampleType(problemType);
            ex.setData("");

            ClientRequest request = new ClientRequest(ServerRequestType.NEW_EXAMPLE);
            request.setUserId(account.getUserId());
            request.setSecurityToken(session.getSecurityToken());
            request.setData(gson.toJson(ex));

            TutorReply reply = SvcFacade.instance().tutorRequest(request);

            if (reply != null && !":ERR".equals(reply.getStatus())) {
                PendingTask pendingTask = gson.fromJson(reply.getData(), PendingTask.class);

                if (pendingTask != null) {
                    session.addCurrentTask(pendingTask);
                    MainFrame.instance().setModel(session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles when a user clicks on a lesson label to select it.
     *
     * @param lessonName the name of the selected lesson
     * @param studyMode the study mode category ("Teach Me", "Practice", "Quiz
     * Me")
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

        mode = switch (studyMode) {
            case "Teach Me" -> TutoringMode.SEE_ONE;
            case "Practice" -> TutoringMode.DO_ONE;
            case "Quiz Me" -> TutoringMode.TEACH_ONE;
            default -> TutoringMode.SEE_ONE;
        };

        model.getStudent().getStudentModel().setTutoringMode(mode);

        loadRequestedLesson(lessonName);

        MainFrame.instance().displayView(MainFrame.ViewName.TUTOR);

        StepSelection selection = getStepSelectionForLesson(lessonName);
        if (selection != null) {
            MainFrame.instance().displayStep(selection);
        }
    }

    private void showDeveloperModeSettings() {
        if (model == null || model.getStudent() == null || model.getStudent().getAccount() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please sign in before changing settings.",
                    "Settings",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String userId = model.getStudent().getAccount().getUserId();

        JCheckBox enableDeveloperMode = new JCheckBox("Enable developer mode");
        JComboBox<TutoringMode> modeBox = new JComboBox<>(TutoringMode.values());

        modeBox.setSelectedItem(model.getStudent().getStudentModel().getTutoringMode());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(enableDeveloperMode, gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("Tutoring mode:"), gbc);

        gbc.gridx = 1;
        panel.add(modeBox, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Developer Mode Settings",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            TutoringMode selectedMode = (TutoringMode) modeBox.getSelectedItem();
            DeveloperModeDAO dao = new DeveloperModeDAO();

            dao.updateDeveloperMode(userId, enableDeveloperMode.isSelected(), selectedMode);

            if (enableDeveloperMode.isSelected()) {
                model.setTutoringMode(selectedMode);
                MainFrame.instance().setModel(model);
                rebuildDashboard();
                updateAllProgressBars();
            }

            JOptionPane.showMessageDialog(this,
                    "Developer mode settings saved. Sign in again to test the full login override.",
                    "Settings Saved",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NonRecoverableException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save developer mode settings.",
                    "Settings Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
}
