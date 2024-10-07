class GameState {
    private int totalScore;
    private int currentMonth;
    private static final int MAX_MONTHS = 12;

    public GameState() {
        this.totalScore = 0;
        this.currentMonth = 1;
    }

    public void updateScore(int monthlyScore) {
        this.totalScore += monthlyScore;
    }

    public void incrementMonth() {
        if (currentMonth < MAX_MONTHS) {
            currentMonth++;
        }
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public boolean isGameOver() {
        return currentMonth > MAX_MONTHS;
    }

    public void reset() {
        this.totalScore = 0;
        this.currentMonth = 1;
    }

    public String getGameStatus() {
        return String.format("Month: %d/%d, Total Score: %d", currentMonth, MAX_MONTHS, totalScore);
    }

    public double getAverageScorePerMonth() {
        return (double) totalScore / currentMonth;
    }

    public int getRemainingMonths() {
        return MAX_MONTHS - currentMonth;
    }

    @Override
    public String toString() {
        return getGameStatus();
    }
}
