package com.mycompany.turnbasedgame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import com.mycompany.turnbasedgame.Monster;
import static java.awt.SystemColor.TEXT_HIGHLIGHT;
import java.io.Serializable;

public class GameGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel heroSelectPanel, menuPanel, dungeonPanel, dungeonStepPanel, battlePanel;
    private GameManager gameManager;
    private JLabel heroNameLabel, enemyNameLabel;
    private JButton backBtn = createStyledButton("← Back to Menu", MEDIUM_BG);

    private static final Color DARK_BG = new Color(20, 25, 40);
    private static final Color MEDIUM_BG = new Color(35, 45, 65);
    private static final Color LIGHT_BG = new Color(50, 65, 90);
    private static final Color ACCENT_COLOR = new Color(100, 200, 255);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color TEXT_PRIMARY = new Color(240, 245, 255);
    private static final Color TEXT_SECONDARY = new Color(180, 190, 200);

    private JLabel stepTitle;
    private int currentStep = 1;
    private int totalSteps = 5;
    private JTextArea stepLog;
    private JProgressBar stepProgress;

    private JLabel heroImg, enemyImg;
    private JLabel heroStats, enemyStats;
    private JLabel heroStatus, enemyStatus;
    private JLabel heroDPT, enemyDPT;
    private JTextArea battleLog;
    private JButton attackBtn, skillBtn, defendBtn, itemBtn, nextBtn;
    private JProgressBar heroHpBar, heroManaBar, enemyHpBar;
    private int restCooldown = 0;

    public GameGUI() {
        setTitle("Dungeon Crawler RPG - Professional Edition");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupCustomTheme();

        gameManager = new GameManager();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializePanels();
        setupMainPanel();

        setIconImage(createGameIcon());
        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "HeroSelect");
    }

    private void setupCustomTheme() {
        UIManager.put("Panel.background", DARK_BG);
        UIManager.put("Button.background", MEDIUM_BG);
        UIManager.put("Button.foreground", TEXT_HIGHLIGHT);
        UIManager.put("Button.focus", ACCENT_COLOR);
        UIManager.put("Label.foreground", TEXT_HIGHLIGHT);
        UIManager.put("TextArea.background", new Color(40, 50, 70));
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground", ACCENT_COLOR);
        UIManager.put("ScrollPane.background", DARK_BG);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollBar.thumb", LIGHT_BG);
        UIManager.put("ScrollBar.track", DARK_BG);
        UIManager.put("RadioButton.background", DARK_BG);
        UIManager.put("RadioButton.foreground", TEXT_PRIMARY);
        UIManager.put("OptionPane.background", DARK_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }

    private Image createGameIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ACCENT_COLOR);
        g2.fillOval(4, 4, 24, 24);
        g2.setColor(GOLD_COLOR);
        g2.fillOval(10, 10, 12, 12);
        g2.dispose();
        return icon;
    }

    private void initializePanels() {
        createHeroSelectPanel();
        createMenuPanel();
        createDungeonPanel();
        createDungeonStepPanel();
        createBattlePanel();
    }

    private void setupMainPanel() {
        mainPanel.add(heroSelectPanel, "HeroSelect");
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(dungeonPanel, "DungeonSelect");
        mainPanel.add(dungeonStepPanel, "DungeonStep");
        mainPanel.add(battlePanel, "Battle");
    }

    private void createHeroSelectPanel() {
        heroSelectPanel = new JPanel();
        heroSelectPanel.setBackground(DARK_BG);
        heroSelectPanel.setLayout(new BorderLayout());

        //title panel with gradient background
        JPanel titlePanel = new GradientPanel(DARK_BG, MEDIUM_BG);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        JLabel heroTitle = new JLabel("Choose Your Hero");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        heroTitle.setForeground(TEXT_PRIMARY);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select your champion to begin the adventure");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(heroTitle);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitle);

        //hero selection panel
        JPanel heroChoicePanel = new JPanel();
        heroChoicePanel.setBackground(DARK_BG);
        heroChoicePanel.setLayout(new GridLayout(2, 2, 20, 20));
        heroChoicePanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        String[] heroes = {"Wizard", "Warrior", "Archer", "Assassin"};
        String[] descriptions = {
            "Master of arcane arts and magical destruction",
            "Mighty fighter with sword and shield",
            "Swift ranger with deadly accuracy",
            "Shadow dancer with lethal precision"
        };
        ButtonGroup heroGroup = new ButtonGroup();

        for (int i = 0; i < heroes.length; i++) {
            JPanel heroCard = createHeroCard(heroes[i], descriptions[i], i == 0);
            heroChoicePanel.add(heroCard);

            //add to button group for radio button functionality
            JRadioButton hiddenRadio = new JRadioButton();
            hiddenRadio.setActionCommand(heroes[i]);
            hiddenRadio.setSelected(i == 0);
            heroGroup.add(hiddenRadio);

            //make card clickable
            heroCard.putClientProperty("radio", hiddenRadio);
            heroCard.addMouseListener(new HeroCardClickListener(heroCard, hiddenRadio));
        }

        //vontinue button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);

        JButton selectHeroBtn = createStyledButton("Begin Adventure", ACCENT_COLOR);
        selectHeroBtn.setPreferredSize(new Dimension(200, 50));
        selectHeroBtn.addActionListener(e -> {
            ButtonModel selection = heroGroup.getSelection();
            if (selection == null) {
                JOptionPane.showMessageDialog(this, "Please select a hero.");
                return;
            }
            String selected = selection.getActionCommand();
            selectHero(selected);
        });

        buttonPanel.add(selectHeroBtn);

        heroSelectPanel.add(titlePanel, BorderLayout.NORTH);
        heroSelectPanel.add(heroChoicePanel, BorderLayout.CENTER);
        heroSelectPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void selectHero(String heroType) {
        switch (heroType.toLowerCase()) {
            case "wizard":
                gameManager.hero = new Wizard();
                break;
            case "warrior":
                gameManager.hero = new Warrior();
                break;
            case "archer":
                gameManager.hero = new Archer();
                break;
            case "assassin":
                gameManager.hero = new Assassin();
                break;
        }
        gameManager.hero.initializeSkills();
        cardLayout.show(mainPanel, "Menu");
        JOptionPane.showMessageDialog(this, "Welcome, " + heroType + "! Your journey begins...");
    }

    private JPanel createHeroCard(String heroName, String description, boolean selected) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(selected ? new Color(55, 70, 95) : MEDIUM_BG);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //add hero image
        String imagePath = "C:\\Users\\Admin\\Documents\\NetBeansProjects\\TurnBasedGame\\src\\main\\java\\com\\mycompany\\turnbasedgame\\images\\"
                + heroName.toLowerCase() + ".png";
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaled = icon.getImage().getScaledInstance(100, 120, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(scaled));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel nameLabel = new JLabel(heroName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
//        nameLabel.setForeground(selected ? ACCENT_COLOR : TEXT_HIGHLIGHT);

        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(TEXT_PRIMARY);

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(nameLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private void createMenuPanel() {
        menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(DARK_BG);

        //header
        JPanel headerPanel = new GradientPanel(DARK_BG, MEDIUM_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JLabel title = new JLabel("Dungeon Hunter", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(TEXT_PRIMARY);
        headerPanel.add(title);

        //menu options
        JPanel menuOptionsPanel = new JPanel(new GridBagLayout());
        menuOptionsPanel.setBackground(DARK_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);

        String[] menuOptions = {
            "Start Dungeon Haunting",
            "Manage Character",
            "Select Hero",
            "Fountain (Restore HP/Mana)",
            "Dungeon Progress",
            "Save Game",
            "Load Game",
            "Exit Game"
        };

        for (int i = 0; i < menuOptions.length; i++) {
            String option = menuOptions[i];
            JButton btn = createStyledButton(option,
                    i == 0 ? SUCCESS_COLOR : MEDIUM_BG);
            btn.setPreferredSize(new Dimension(300, 45));
            btn.addActionListener(e -> handleMenuOption(option));

            gbc.gridy = i;
            menuOptionsPanel.add(btn, gbc);
        }

        menuPanel.add(headerPanel, BorderLayout.NORTH);
        menuPanel.add(menuOptionsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatRow(String label, JProgressBar bar) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.setBackground(MEDIUM_BG);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel lblLabel = new JLabel(label);
//        lblLabel.setForeground(TEXT_HIGHLIGHT);
        lblLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        lblLabel.setPreferredSize(new Dimension(60, 20));

        row.add(lblLabel, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);

        return row;
    }

    private void styleProgressBar(JProgressBar bar, Color color) {
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(0, 18));
        bar.setBackground(new Color(30, 30, 40));
        bar.setForeground(color);
        bar.setBorder(BorderFactory.createEmptyBorder());
        bar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();

                super.paintComponent(g);
            }
        };

//        button.setForeground(TEXT_HIGHLIGHT);
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        return button;
    }

    private void createDungeonPanel() {
        dungeonPanel = new JPanel(new BorderLayout());
        dungeonPanel.setBackground(DARK_BG);

        //header
        JPanel headerPanel = new GradientPanel(DARK_BG, MEDIUM_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel dungeonTitle = new JLabel("Select Your Dungeon", SwingConstants.CENTER);
        dungeonTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        dungeonTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(dungeonTitle);

        //dungeon grid
        JPanel dungeonGrid = new JPanel(new GridLayout(4, 3, 15, 15));
        dungeonGrid.setBackground(DARK_BG);
        dungeonGrid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        String[] dungeonNames = {
            "Shadow Crypt", "Whispering Woods", "Frozen Caverns",
            "Dragonspire Keep", "Molten Depths", "Bandit Stronghold",
            "Abyssal Sanctum", "Crystal Mines", "Ancient Ruins",
            "Dragon's Lair", "Void Citadel", "World Tree"
        };

        String[] difficulties = {"TIER 1", "TIER 1", "TIER 1", "TIER 2", "TIER 2", "TIER 2",
            "TIER 3", "TIER 3", "TIER 3", "TIER 4", "TIER 4", "TIER 4"};

        for (int i = 0; i < 12; i++) {
            JPanel dungeonCard = createDungeonCard(dungeonNames[i], difficulties[i], i + 1);
            dungeonGrid.add(dungeonCard);
        }

        //back button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));
        buttonPanel.add(backBtn);

        dungeonPanel.add(headerPanel, BorderLayout.NORTH);
        dungeonPanel.add(new JScrollPane(dungeonGrid), BorderLayout.CENTER);
        dungeonPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createDungeonCard(String name, String tier, int dungeonNum) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MEDIUM_BG);
        card.setBorder(new RoundedBorder(TEXT_SECONDARY, 1, 10));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY); //nsure high contrast

        JLabel tierLabel = new JLabel(tier, SwingConstants.CENTER);
        tierLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tierLabel.setForeground(getTierColor(tier)); //yhis is fine for colored tiers

        card.add(nameLabel, BorderLayout.CENTER);
        card.add(tierLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleDungeonSelect(dungeonNum);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(LIGHT_BG);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(MEDIUM_BG);
            }
        });

        return card;
    }

    private Color getTierColor(String tier) {
        switch (tier) {
            case "TIER 1":
                return SUCCESS_COLOR;
            case "TIER 2":
                return GOLD_COLOR;
            case "TIER 3":
                return new Color(255, 152, 0);
            case "TIER 4":
                return DANGER_COLOR;
            default:
                return TEXT_SECONDARY;
        }
    }

    private void createDungeonStepPanel() {
        dungeonStepPanel = new JPanel(new BorderLayout());
        dungeonStepPanel.setBackground(DARK_BG);

        //header with progress
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(MEDIUM_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        stepTitle = new JLabel("Dungeon Step 1/5", SwingConstants.CENTER);
        stepTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        stepTitle.setForeground(TEXT_PRIMARY);

        stepProgress = new JProgressBar(0, 5);
        stepProgress.setValue(1);
        stepProgress.setStringPainted(true);
        stepProgress.setString("Step 1 of 5");
        stepProgress.setPreferredSize(new Dimension(300, 25));
        styleProgressBar(stepProgress, ACCENT_COLOR);

        JPanel progressPanel = new JPanel(new FlowLayout());
        progressPanel.setBackground(MEDIUM_BG);
        progressPanel.add(stepProgress);

        headerPanel.add(stepTitle, BorderLayout.NORTH);
        headerPanel.add(progressPanel, BorderLayout.SOUTH);

        //action buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        actionPanel.setBackground(DARK_BG);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton advanceBtn = createStyledButton("Advance", SUCCESS_COLOR);
        JButton restBtn = createStyledButton("Rest", new Color(76, 175, 80));
        JButton scoutBtn = createStyledButton("Scout", new Color(33, 150, 243));
        JButton skipBtn = createStyledButton("Skip Monster", new Color(255, 152, 0));

        actionPanel.add(advanceBtn);
        actionPanel.add(restBtn);
        actionPanel.add(scoutBtn);
        actionPanel.add(skipBtn);

        //logg area
        stepLog = new JTextArea(10, 60);
        stepLog.setEditable(false);
        stepLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        stepLog.setBackground(DARK_BG);
        stepLog.setForeground(TEXT_PRIMARY);
        stepLog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane logScroll = new JScrollPane(stepLog);
        logScroll.setBorder(new RoundedBorder(TEXT_SECONDARY, 1, 10));

        //setup button actions
        setupDungeonStepActions(advanceBtn, restBtn, scoutBtn, skipBtn);

        dungeonStepPanel.add(headerPanel, BorderLayout.NORTH);
        dungeonStepPanel.add(actionPanel, BorderLayout.CENTER);
        dungeonStepPanel.add(logScroll, BorderLayout.SOUTH);
    }

    private void createBattlePanel() {
        battlePanel = new JPanel(new BorderLayout());
        battlePanel.setBackground(DARK_BG);

        //character display panel
        JPanel characterPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        characterPanel.setBackground(DARK_BG);
        characterPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel heroPanel = createCharacterPanel(true);
        JPanel enemyPanel = createCharacterPanel(false);

        characterPanel.add(heroPanel);
        characterPanel.add(enemyPanel);

        //battle log
        battleLog = new JTextArea(8, 50);
        battleLog.setEditable(false);
        battleLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        battleLog.setBackground(DARK_BG);
        battleLog.setForeground(TEXT_PRIMARY);
        battleLog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane battleScroll = new JScrollPane(battleLog);
        battleScroll.setBorder(new RoundedBorder(TEXT_SECONDARY, 1, 10));
        battleScroll.setPreferredSize(new Dimension(0, 200));

        //action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        actionPanel.setBackground(DARK_BG);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(15, 40, 20, 40));

        attackBtn = createStyledButton("Attack", DANGER_COLOR);
        skillBtn = createStyledButton("Skill", new Color(156, 39, 176));
        defendBtn = createStyledButton("Defend", new Color(96, 125, 139));
        itemBtn = createStyledButton("Item", new Color(255, 152, 0));
        nextBtn = createStyledButton("Continue", SUCCESS_COLOR);

        actionPanel.add(attackBtn);
        actionPanel.add(skillBtn);
        actionPanel.add(defendBtn);
        actionPanel.add(itemBtn);
        actionPanel.add(nextBtn);

        nextBtn.setEnabled(false);

        //setup battle actions
        setupBattleActions();

        battlePanel.add(characterPanel, BorderLayout.NORTH);
        battlePanel.add(battleScroll, BorderLayout.CENTER);
        battlePanel.add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createCharacterPanel(boolean isHero) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MEDIUM_BG);
        panel.setBorder(new RoundedBorder(TEXT_SECONDARY, 1, 15));

        //name label at the top
        JLabel nameLabel = new JLabel("", isHero ? SwingConstants.LEFT : SwingConstants.RIGHT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(ACCENT_COLOR);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        if (isHero) {
            heroNameLabel = nameLabel;
        } else {
            enemyNameLabel = nameLabel;
        }
        panel.add(nameLabel, BorderLayout.NORTH);

        //character imagea
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(MEDIUM_BG);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(114, 142));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(LIGHT_BG);
        imgLabel.setBorder(new RoundedBorder(TEXT_SECONDARY, 1, 10));
        imgLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        imgLabel.setForeground(TEXT_PRIMARY);

        if (isHero) {
            heroImg = imgLabel;
        } else {
            enemyImg = imgLabel;
        }

        imagePanel.add(imgLabel, BorderLayout.CENTER);

        //stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(MEDIUM_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        //health bar
        JProgressBar hpBar = new JProgressBar();
        styleProgressBar(hpBar, DANGER_COLOR);

        //mana bar (always, but only update for hero)
        JProgressBar manaBar = new JProgressBar();
        styleProgressBar(manaBar, new Color(33, 150, 243));

        if (isHero) {
            heroStats = new JLabel();
            heroStatus = new JLabel();
            heroDPT = new JLabel();
            heroHpBar = hpBar;
            heroManaBar = manaBar;
        } else {
            enemyStats = new JLabel();
            enemyStatus = new JLabel();
            enemyDPT = new JLabel();
            enemyHpBar = hpBar;
            // enemyManaBar = manaBar;
            manaBar.setVisible(false); //hide for enemy but still takes up space
        }

        statsPanel.add(createStatRow("Health:", hpBar));
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(createStatRow("Mana:", manaBar)); //always add, hide for enemy
        statsPanel.add(Box.createVerticalStrut(10));
        if (isHero) {
            statsPanel.add(heroStatus);
            statsPanel.add(heroDPT);
        } else {
            statsPanel.add(enemyStatus);
            statsPanel.add(enemyDPT);
        }

        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setupDungeonStepActions(JButton advanceBtn, JButton restBtn, JButton scoutBtn, JButton skipBtn) {
        advanceBtn.addActionListener(e -> {
            DungeonLevel dungeon = gameManager.getCurrentDungeon();
            String event = dungeon.processStep();
            currentStep = dungeon.getCurrentStep();
            totalSteps = dungeon.getMaxSteps();
            updateStepProgress();
            updateStepTitle();
            handleDungeonEvent(event, stepLog);
            if (restCooldown > 0) {
                restCooldown--;
            }
        });

        restBtn.addActionListener(e -> {
            if (restCooldown > 0) {
                JOptionPane.showMessageDialog(this,
                        "You can only rest every 4 steps! (" + restCooldown + " steps left)");
                return;
            }
            updateStepProgress();
            updateStepTitle();
            int restHp = Math.min(30, gameManager.hero.getMaxHp() - gameManager.hero.getHp());
            int restMana = Math.min(15, gameManager.hero.getMaxMana() - gameManager.hero.getMana());
            gameManager.hero.heal(restHp);
            gameManager.hero.restoreMana(restMana);
            stepLog.append("You rest and recover " + restHp + " HP and " + restMana + " Mana.\n");
            restCooldown = 4;
        });

        scoutBtn.addActionListener(e -> {
            DungeonLevel dungeon = gameManager.getCurrentDungeon();
            String nextEvent = dungeon.peekNextEvent();
            updateStepProgress();
            updateStepTitle();
            if (gameManager.hero.getMana() < 15) {
                stepLog.append("Not enough mana to scout ahead!\n");
            } else {
                gameManager.hero.consumeMana(15);
                stepLog.append("You scout ahead and sense: " + nextEvent + " (15 mana spent)\n");
            }
        });

        skipBtn.addActionListener(e -> {
            DungeonLevel dungeon = gameManager.getCurrentDungeon();
            String nextEvent = dungeon.peekNextEvent();
            if (!"monster".equals(nextEvent)) {
                stepLog.append("There's nothing to skip!\n");
                return;
            }
            if (restCooldown > 0) {
                restCooldown--;
            }
            if (gameManager.hero.getMana() < 50) {
                stepLog.append("Not enough mana to skip the monster!\n");
                return;
            }
            gameManager.hero.consumeMana(50);
            dungeon.processStep();
            stepLog.append("You skip the monster (50 mana spent).\n");
            currentStep = dungeon.getCurrentStep();
            totalSteps = dungeon.getMaxSteps();
            updateStepTitle();
            updateStepProgress();
        });
    }

    private void setupBattleActions() {
        attackBtn.addActionListener(e -> {
            String result = gameManager.playerAttack();
            battleLog.append(result + "\n");
            updateBattleStats();
            updateStatusEffects();
            updateDPT();
            if (gameManager.currentEnemy.isAlive()) {
                enemyTurn();
            } else {
                handleBattleVictory();
            }
        });

        skillBtn.addActionListener(e -> {
            List<String> skills = gameManager.getHeroSkillNames();
            if (skills.isEmpty()) {
                battleLog.append("No skills available!\n");
                return;
            }
            String skill = (String) JOptionPane.showInputDialog(this, "Choose a skill:", "Skills",
                    JOptionPane.PLAIN_MESSAGE, null, skills.toArray(), skills.get(0));
            if (skill != null) {
                String result = gameManager.playerUseSkill(skill);
                battleLog.append(result + "\n");
                updateBattleStats();
                updateStatusEffects();
                updateDPT();
                if (gameManager.currentEnemy.isAlive()) {
                    enemyTurn();
                } else {
                    handleBattleVictory();
                }
            }
        });

        defendBtn.addActionListener(e -> {
            String result = gameManager.playerDefend();
            battleLog.append(result + "\n");
            updateBattleStats();
            updateStatusEffects();
            updateDPT();
            enemyTurn();
        });

        itemBtn.addActionListener(e -> {
            List<Item> items = gameManager.getHeroItemNames().stream()
                    .map(name -> gameManager.hero.getInventory().getItems().stream()
                    .filter(item -> item.getName().equals(name)).findFirst().orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            if (items.isEmpty()) {
                battleLog.append("No items available!\n");
                return;
            }
            String[] itemNames = items.stream()
                    .map(item -> item.getName() + " - " + item.getDescription())
                    .toArray(String[]::new);
            int idx = JOptionPane.showOptionDialog(this, "Choose an item to use:", "Items",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, itemNames, itemNames[0]);
            if (idx >= 0) {
                Item item = items.get(idx);
                String result = gameManager.playerUseItem(item.getName());
                battleLog.append(result + "\n");
                updateBattleStats();
                updateStatusEffects();
                updateDPT();
                enemyTurn();
            }
        });

        nextBtn.addActionListener(e -> {
            if (gameManager.getCurrentDungeon().isComplete()) {
                cardLayout.show(mainPanel, "Menu");
            } else {
                cardLayout.show(mainPanel, "DungeonStep");
                enableBattleActions();
            }
        });
    }

    private void handleMenuOption(String option) {
        switch (option) {
            case "Start Dungeon Haunting":
                cardLayout.show(mainPanel, "DungeonSelect");
                break;
            case "Manage Character":
                Player hero = gameManager.hero;
                String info = "<html><b>Name:</b> " + hero.getName()
                        + "  <b>Level:</b> " + hero.getLevel()
                        + "  <b>EXP:</b> " + hero.exp + "/100<br>"
                        + "<b>HP:</b> " + hero.getHp() + "/" + hero.getMaxHp()
                        + "  <b>Mana:</b> " + hero.getMana() + "/" + hero.getMaxMana() + "<br>"
                        + "<b>Physical:</b> " + hero.getPhysical()
                        + "  <b>Magical:</b> " + hero.getMagical()
                        + "  <b>Defense:</b> " + hero.getDefense() + "<br>"
                        + "<b>Status:</b> " + gameManager.getHeroStatusEffects() + "</html>";
                JOptionPane.showMessageDialog(this, info, "Character Sheet", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Select Hero":
                cardLayout.show(mainPanel, "HeroSelect");
                break;
            case "Fountain (Restore HP/Mana)":
                gameManager.hero.heal(gameManager.hero.getMaxHp());
                gameManager.hero.restoreMana(gameManager.hero.getMaxMana());
                JOptionPane.showMessageDialog(this, "You are fully healed and your mana is restored at the Fountain!");
                break;
            case "Dungeon Progress":
                StringBuilder sb = new StringBuilder();
                int completed = 0;
                for (int i = 0; i < gameManager.dungeons.length; i++) {
                    String status = gameManager.dungeonCompleted[i] ? "✓ COMPLETED" : "○ Not Completed";
                    sb.append(gameManager.dungeons[i].getName()).append(" - ").append(status).append("\n");
                    if (gameManager.dungeonCompleted[i]) {
                        completed++;
                    }
                }
                sb.append("\nOverall Progress: ").append(completed).append("/").append(gameManager.dungeons.length)
                        .append(" (").append(completed * 100 / gameManager.dungeons.length).append("%)");
                JOptionPane.showMessageDialog(this, sb.toString(), "Dungeon Progress", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Save Game":
                gameManager.saveGame();
                JOptionPane.showMessageDialog(this, "Game saved successfully!");
                break;
            case "Load Game":
                GameManager loaded = GameManager.loadGame();
                if (loaded != null) {
                    this.gameManager = loaded;
                    JOptionPane.showMessageDialog(this, "Game loaded successfully!");
                    updateBattleStats();
                    updateStatusEffects();
                    updateDPT();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to load game.");
                }
                break;
            case "Exit Game":
                System.exit(0);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Option not implemented yet.");
        }
    }

    private void handleDungeonSelect(int dungeonNum) {
        if (dungeonNum == 13) {
            cardLayout.show(mainPanel, "Menu");
            return;
        }
        if (dungeonNum < 1 || dungeonNum > gameManager.dungeons.length) {
            JOptionPane.showMessageDialog(this, "Invalid dungeon selection.");
            return;
        }
        gameManager.setCurrentDungeonIndex(dungeonNum - 1);
        DungeonLevel dungeon = gameManager.getCurrentDungeon();
        dungeon.reset();
        currentStep = 1;
        totalSteps = dungeon.getMaxSteps();
        updateStepTitle();
        updateStepProgress();
        stepLog.setText("");
        cardLayout.show(mainPanel, "DungeonStep");
    }

    private void handleDungeonEvent(String event, JTextArea stepLog) {
        DungeonLevel dungeon = gameManager.getCurrentDungeon();
        switch (event) {
            case "monster":
                Monster monster = dungeon.getRandomMonster();
                gameManager.currentEnemy = monster;
                showBattlePanel();
                break;
            case "boss":
                Monster boss = dungeon.getBoss();
                gameManager.currentEnemy = boss;
                gameManager.enemyTurn();
                showBattlePanel();
                break;
            case "loot":
                Item loot = dungeon.getRandomLoot();
                stepLog.append("You found loot: " + loot.getName() + "!\n");
                gameManager.hero.getInventory().addItem(loot);
                break;
            case "special":
                String special = dungeon.getSpecialEvent();
                handleSpecialEvent(special);
                break;
            case "empty":
            default:
                stepLog.append("The path is quiet... nothing happens.\n");
        }
    }

    private void showBattlePanel() {
        Player hero = gameManager.hero;
        Monster enemy = gameManager.currentEnemy;

        //hero name and image
        heroNameLabel.setText(hero.getName());
        String heroImagePath = "C:\\Users\\Admin\\Documents\\NetBeansProjects\\TurnBasedGame\\src\\main\\java\\com\\mycompany\\turnbasedgame\\images\\"
                + hero.getHeroType().toLowerCase() + ".png";
        ImageIcon heroIcon = new ImageIcon(heroImagePath);
        Image heroScaled = heroIcon.getImage().getScaledInstance(114, 142, Image.SCALE_SMOOTH);
        heroImg.setIcon(new ImageIcon(heroScaled));

        heroHpBar.setMaximum(hero.getMaxHp());
        heroHpBar.setValue(hero.getHp());
        heroHpBar.setString(hero.getHp() + "/" + hero.getMaxHp());
        heroManaBar.setMaximum(hero.getMaxMana());
        heroManaBar.setValue(hero.getMana());
        heroManaBar.setString(hero.getMana() + "/" + hero.getMaxMana());

        //enemy name and image
        enemyNameLabel.setText(enemy.getName());
        String enemyImagePath = "C:\\Users\\Admin\\Documents\\NetBeansProjects\\TurnBasedGame\\src\\main\\java\\com\\mycompany\\turnbasedgame\\images\\"
                + enemy.getName().toLowerCase().replace(" ", "_") + ".png";
        ImageIcon enemyIcon = new ImageIcon(enemyImagePath);
        Image enemyScaled = enemyIcon.getImage().getScaledInstance(114, 142, Image.SCALE_SMOOTH);
        enemyImg.setIcon(new ImageIcon(enemyScaled));

        enemyHpBar.setMaximum(enemy.getMaxHp());
        enemyHpBar.setValue(enemy.getHp());
        enemyHpBar.setString(enemy.getHp() + "/" + enemy.getMaxHp());

        updateStatusEffects();
        updateDPT();

        battleLog.setText("A wild " + enemy.getName() + " appears!\n");
        nextBtn.setEnabled(false);
        enableBattleActions();
        cardLayout.show(mainPanel, "Battle");
    }

    private void updateStepTitle() {
        stepTitle.setText("Dungeon Step " + currentStep + "/" + totalSteps);
    }

    private void updateStepProgress() {
        stepProgress.setMaximum(totalSteps);
        stepProgress.setValue(currentStep);
        stepProgress.setString("Step " + currentStep + " of " + totalSteps);
    }

    private void updateBattleStats() {
        Player hero = gameManager.hero;
        Monster enemy = gameManager.currentEnemy;

        heroHpBar.setMaximum(hero.getMaxHp());
        heroHpBar.setValue(hero.getHp());
        heroHpBar.setString(hero.getHp() + "/" + hero.getMaxHp());

        heroManaBar.setMaximum(hero.getMaxMana());
        heroManaBar.setValue(hero.getMana());
        heroManaBar.setString(hero.getMana() + "/" + hero.getMaxMana());

        enemyHpBar.setMaximum(enemy.getMaxHp());
        enemyHpBar.setValue(enemy.getHp());
        enemyHpBar.setString(enemy.getHp() + "/" + enemy.getMaxHp());
    }

    private void updateStatusEffects() {
        heroStatus
                .setText("<html><b>Status:</b> " + gameManager.hero.getStatusEffects().getEffectSummary() + "</html>");
        enemyStatus.setText(
                "<html><b>Status:</b> " + gameManager.currentEnemy.getStatusEffects().getEffectSummary() + "</html>");
    }

    private void updateDPT() {
        heroDPT.setText("<html><b>Damage/Turn:</b> " + gameManager.getHeroDPT() + "</html>");
        enemyDPT.setText("<html><b>Damage/Turn:</b> " + gameManager.getEnemyDPT() + "</html>");
    }

    private void enemyTurn() {
        String result = gameManager.enemyTurn();
        battleLog.append(result + "\n");
        updateBattleStats();
        updateStatusEffects();
        updateDPT();
        if (!gameManager.hero.isAlive()) {
            battleLog.append("You have been defeated!\n");
            disableBattleActions();
            nextBtn.setEnabled(true);
        }

    }

    private void handleBattleVictory() {
        battleLog.append("You defeated " + gameManager.currentEnemy.getName() + "!\n");
        int exp = gameManager.currentEnemy.getExpReward();
        gameManager.hero.gainExperience(exp);
        battleLog.append("You gained " + exp + " EXP!\n");
        Item loot = gameManager.getCurrentDungeon().getRandomLoot();
        if (loot != null) {
            gameManager.hero.getInventory().addItem(loot);
            battleLog.append("You found loot: " + loot.getName() + "!\n");
        }
        nextBtn.setEnabled(true);
        disableBattleActions();

        if (gameManager.getCurrentDungeon().isComplete()) {
            gameManager.getDungeonCompleted()[gameManager.currentDungeonIndex] = true;
            JOptionPane.showMessageDialog(this, "Dungeon complete! You may return to menu.");
        }
    }

    private void disableBattleActions() {
        attackBtn.setEnabled(false);
        skillBtn.setEnabled(false);
        defendBtn.setEnabled(false);
        itemBtn.setEnabled(false);
    }

    private void enableBattleActions() {
        attackBtn.setEnabled(true);
        skillBtn.setEnabled(true);
        defendBtn.setEnabled(true);
        itemBtn.setEnabled(true);
    }

    private class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.bottom = insets.top = radius;
            return insets;
        }

    }

    private static class HeroCardClickListener extends MouseAdapter {

        private final JPanel card;
        private final JRadioButton radio;

        public HeroCardClickListener(JPanel card, JRadioButton radio) {
            this.card = card;
            this.radio = radio;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            radio.setSelected(true);
            for (Component comp : card.getParent().getComponents()) {
                if (comp instanceof JPanel) {
                    comp.setBackground(MEDIUM_BG);
                }
            }
            card.setBackground(LIGHT_BG);
        }
    }

    private void handleSpecialEvent(String special) {
        switch (special) {
            case "merchant":
                JOptionPane.showMessageDialog(this,
                        "A mysterious merchant appears!\nHe gives you a small gift and disappears.");
                gameManager.hero.heal(25);
                gameManager.hero.restoreMana(20);
                break;
            case "shrine":
                JOptionPane.showMessageDialog(this,
                        "You discover an ancient shrine!\nPhysical and Magical power increased by 2!");
                gameManager.hero.setPhysical(gameManager.hero.getPhysical() + 2);
                gameManager.hero.setMagical(gameManager.hero.getMagical() + 2);
                break;
            case "trap":
                String[] trapOptions = {"Dodge (20 mana)", "Shield (reduced damage)", "Tank it (full damage)"};
                int trapChoice = JOptionPane.showOptionDialog(this, "You step on a trap! Choose your reaction:",
                        "Trap!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, trapOptions,
                        trapOptions[0]);
                if (trapChoice == 0 && gameManager.hero.getMana() >= 20) {
                    gameManager.hero.consumeMana(20);
                    JOptionPane.showMessageDialog(this, "You dodge the trap and take no damage!");
                } else if (trapChoice == 1) {
                    gameManager.hero.takeDamage(10);
                    JOptionPane.showMessageDialog(this, "You shield yourself and take 10 damage!");
                } else {
                    gameManager.hero.takeDamage(25);
                    JOptionPane.showMessageDialog(this, "You tank the trap and take 25 damage!");
                }
                break;
            case "puzzle":
                String[] puzzleOptions = {"Fire", "Plant", "Wind", "Skip"};
                int puzzleChoice = JOptionPane.showOptionDialog(this,
                        "Riddle: 'I am not alive, but I grow; I don't have lungs, but I need air.'",
                        "Puzzle!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, puzzleOptions,
                        puzzleOptions[0]);
                if (puzzleChoice == 0) {
                    JOptionPane.showMessageDialog(this, "Correct! You are rewarded with 30 mana.");
                    gameManager.hero.restoreMana(30);
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect! You take 15 damage.");
                    gameManager.hero.takeDamage(15);
                }
                break;
            case "treasure_room":
                String[] chestOptions = {"Golden Chest", "Silver Chest", "Wooden Chest"};
                int chestChoice = JOptionPane.showOptionDialog(this, "Three chests lie before you. Choose one:",
                        "Treasure Room", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                        chestOptions, chestOptions[0]);
                Item loot = gameManager.getCurrentDungeon().getRandomLoot();
                gameManager.hero.getInventory().addItem(loot);
                JOptionPane.showMessageDialog(this,
                        "You open the chest and find: " + loot.getName() + "\n" + loot.getDescription());
                break;
            default:
                JOptionPane.showMessageDialog(this, "Nothing happens...");
        }
        updateBattleStats();
        updateStatusEffects();
        updateDPT();
    }

    private class GradientPanel extends JPanel implements Serializable {

        private final Color color1;
        private final Color color2;

        public GradientPanel(Color color1, Color color2) {
            this.color1 = color1;
            this.color2 = color2;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GameGUI().setVisible(true);
        });
    }
}
