class LunarModule extends Building {
    private int moduleType;

    public LunarModule(int id, int x, int y, int moduleType) {
        super(id, x, y);
        this.moduleType = moduleType;
    }

    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public String formatString() {
        return String.format("%d %d %d %d", id, x, y, moduleType);
    }
}
