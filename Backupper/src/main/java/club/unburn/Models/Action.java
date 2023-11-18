package club.unburn.Models;

import java.util.ArrayList;
import java.util.List;

public class Action {
	public String name = "Example Name";
	public List<String> filepaths = new ArrayList<String>();
	public List<String> toexportdb = new ArrayList<String>();
	public String database = "mysql";
	public String interval = "1d";
	public String exportname = "%date%-%name%";
	public String compressing = "zip";
	public String decaying = "4w";
}
