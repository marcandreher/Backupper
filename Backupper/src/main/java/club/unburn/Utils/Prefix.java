package club.unburn.Utils;

public enum Prefix {
	INFO(Color.CYAN + "[INFO]: " + Color.RESET), BUILD_BACKUP(Color.GREEN + "[" + Color.GREEN_BOLD + "Build" + Color.GREEN + "] " + Color.RESET), ERROR(Color.RED + "[ERROR]: " + Color.RESET),
	IMPORTANT(Color.RED + "[IMPORTANT]: " + Color.RED), WARNING(Color.YELLOW + "[WARNING]: " + Color.RESET),
	MYSQL(Color.BLUE + "[MYSQL]: " + Color.RESET), INPUT(Color.MAGENTA + "[INPUT]: " + Color.RESET), OPENID(Color.GREEN + "[OPENID]: " + Color.RESET);

	private final String code;

	Prefix(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}
}