package de.mc8051.arma3launcher;

/**
 * Created by gurkengewuerz.de on 30.03.2020.
 */
public enum Parameters {

    LANGUAGE("language", Parameter.ParameterType.CLIENT, "", new String[]{"system", "en_US", "de_DE"}),
    BEHAVIOUR_AFTER_START("behaviourAfterStart", Parameter.ParameterType.CLIENT, "", new String[]{"nothing", "minimize", "exit"}),
    SHOW_START_PARAMETER("ShowStartParameter", Parameter.ParameterType.CLIENT),
    CHECK_MODSET("CheckModset", Parameter.ParameterType.CLIENT),
    USE_WORKSHOP("UseWorkshop", Parameter.ParameterType.CLIENT),
    ARMA_PATH("armaPath", Parameter.ParameterType.CLIENT),
    MOD_PATH("modPath", Parameter.ParameterType.CLIENT),

    PROFILE("Profile", Parameter.ParameterType.ARMA, "name"),
    USE_64_BIT_CLIENT("Use64BitClient", Parameter.ParameterType.ARMA, "Use64BitClient"),
    NO_SPLASH("NoSplash", Parameter.ParameterType.ARMA, "noSplash"),
    SKIP_INTRO("SkipIntro", Parameter.ParameterType.ARMA, "skipIntro"),
    WORLD("World", Parameter.ParameterType.ARMA, "world"),
    MAX_MEM("MaxMem", Parameter.ParameterType.ARMA, "maxMem"),
    MAX_VRAM("MaxVRAM", Parameter.ParameterType.ARMA, "maxVRAM"),
    NO_CB("NoCB", Parameter.ParameterType.ARMA, "noCB"),
    CPU_COUNT("CpuCount", Parameter.ParameterType.ARMA, "cpuCount"),
    EXTRA_THREADS("ExThreads", Parameter.ParameterType.ARMA, "exThreads", new String[]{"", "3", "7"}),
    MALLOC("Malloc", Parameter.ParameterType.ARMA, "malloc", new String[]{"", "tbb4malloc_bi", "jemalloc_bi", "system"}),
    NO_LOGS("NoLogs", Parameter.ParameterType.ARMA, "noLogs"),
    ENABLE_HT("EnableHT", Parameter.ParameterType.ARMA, "enableHT"),
    HUGEPAGES("Hugepages", Parameter.ParameterType.ARMA, "hugepages"),
    NO_PAUSE("NoPause", Parameter.ParameterType.ARMA, "noPause"),
    SHOW_SCRIPT_ERRORS("ShowScriptErrors", Parameter.ParameterType.ARMA, "showScriptErrors"),
    FILE_PATCHING("FilePatching", Parameter.ParameterType.ARMA, "filePatching"),
    INIT("Init", Parameter.ParameterType.ARMA, "init"),
    BETA("Beta", Parameter.ParameterType.ARMA, "beta"),
    CRASH_DIAG("CrashDiag", Parameter.ParameterType.ARMA, "crashDiag"),
    WINDOW("Window", Parameter.ParameterType.ARMA, "window"),
    POS_X("PosX", Parameter.ParameterType.ARMA, "posX"),
    POS_Y("PosY", Parameter.ParameterType.ARMA, "posY");

    private String name;
    private Parameter.ParameterType type;
    private String[] values;
    private String startParameter;

    Parameters(String name, Parameter.ParameterType type) {
        this(name, type, "");
    }

    Parameters(String name, Parameter.ParameterType type, String startParameter) {
        this(name, type, startParameter, null);
    }

    Parameters(String name, Parameter.ParameterType type, String startParameter, String[] values) {
        this.name = name;
        this.type = type;
        this.startParameter = startParameter;
        this. values = values;
    }

    public Parameter<String> toStringParameter() {
        return new Parameter<String>(name, type, String.class, values, startParameter);
    }

    public Parameter<String> toStringParameter(String... values) {
        return new Parameter<String>(name, type, String.class, values, startParameter);
    }

    public Parameter<Boolean> toBooolParameter() {
        return new Parameter<>(name, type, Boolean.class);
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
