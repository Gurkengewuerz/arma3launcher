package de.mc8051.arma3launcher;

/**
 * Created by gurkengewuerz.de on 30.03.2020.
 */
public enum Parameters {

    LANGUAGE("language", Parameter.ParameterType.CLIENT, String.class, "", new String[]{"system", "en_US", "de_DE"}),
    BEHAVIOUR_AFTER_START("behaviourAfterStart", Parameter.ParameterType.CLIENT, String.class, "", new String[]{"nothing", "minimize", "exit"}),
    SHOW_START_PARAMETER("ShowStartParameter", Parameter.ParameterType.CLIENT, Boolean.class),
    CHECK_MODSET("CheckModset", Parameter.ParameterType.CLIENT, Boolean.class),
    USE_WORKSHOP("UseWorkshop", Parameter.ParameterType.CLIENT, Boolean.class),
    ARMA_PATH("armaPath", Parameter.ParameterType.CLIENT, String.class),
    MOD_PATH("modPath", Parameter.ParameterType.CLIENT, String.class),

    PROFILE("Profile", Parameter.ParameterType.ARMA, String.class, "name"),
    USE_64_BIT_CLIENT("Use64BitClient", Parameter.ParameterType.ARMA, Boolean.class),
    NO_SPLASH("NoSplash", Parameter.ParameterType.ARMA, Boolean.class, "noSplash"),
    SKIP_INTRO("SkipIntro", Parameter.ParameterType.ARMA, Boolean.class, "skipIntro"),
    WORLD("World", Parameter.ParameterType.ARMA, String.class, "world"),
    MAX_MEM("MaxMem", Parameter.ParameterType.ARMA, String.class, "maxMem"),
    MAX_VRAM("MaxVRAM", Parameter.ParameterType.ARMA, String.class, "maxVRAM"),
    NO_CB("NoCB", Parameter.ParameterType.ARMA, Boolean.class, "noCB"),
    CPU_COUNT("CpuCount", Parameter.ParameterType.ARMA, String.class, "cpuCount"),
    EXTRA_THREADS("ExThreads", Parameter.ParameterType.ARMA, String.class, "exThreads", new String[]{"", "3", "7"}),
    MALLOC("Malloc", Parameter.ParameterType.ARMA, String.class, "malloc", new String[]{"", "tbb4malloc_bi", "tbb4malloc_bi_x64", "jemalloc_bi", "jemalloc_bi_x64", "system"}),
    NO_LOGS("NoLogs", Parameter.ParameterType.ARMA, Boolean.class, "noLogs"),
    ENABLE_HT("EnableHT", Parameter.ParameterType.ARMA, Boolean.class, "enableHT"),
    HUGEPAGES("Hugepages", Parameter.ParameterType.ARMA, Boolean.class, "hugepages"),
    NO_PAUSE("NoPause", Parameter.ParameterType.ARMA, Boolean.class, "noPause"),
    SHOW_SCRIPT_ERRORS("ShowScriptErrors", Parameter.ParameterType.ARMA, Boolean.class, "showScriptErrors"),
    FILE_PATCHING("FilePatching", Parameter.ParameterType.ARMA, Boolean.class, "filePatching"),
    INIT("Init", Parameter.ParameterType.ARMA, String.class, "init"),
    BETA("Beta", Parameter.ParameterType.ARMA, String.class, "beta"),
    CRASH_DIAG("CrashDiag", Parameter.ParameterType.ARMA, Boolean.class, "crashDiag"),
    WINDOW("Window", Parameter.ParameterType.ARMA, Boolean.class, "window"),
    POS_X("PosX", Parameter.ParameterType.ARMA, String.class, "posX"),
    POS_Y("PosY", Parameter.ParameterType.ARMA, String.class, "posY");

    private String name;
    private Parameter.ParameterType type;
    private String[] values;
    private String startParameter;
    private Class<?> clazz;

    Parameters(String name, Parameter.ParameterType type, Class<?> clazz) {
        this(name, type, clazz, "");
    }

    Parameters(String name, Parameter.ParameterType type, Class<?> clazz, String startParameter) {
        this(name, type, clazz, startParameter, null);
    }

    Parameters(String name, Parameter.ParameterType type, Class<?> clazz, String startParameter, String[] values) {
        this.name = name;
        this.type = type;
        this.startParameter = startParameter;
        this.values = values;
        this.clazz = clazz;
    }

    public Parameter toParameter() {
        return new Parameter(name, type, clazz, values, startParameter);
    }

    public Parameter toParameter(String... values) {
        return new Parameter(name, type, clazz, values, startParameter);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public Parameter.ParameterType getType() {
        return type;
    }

    public String getStartParameter() {
        return startParameter;
    }
}
