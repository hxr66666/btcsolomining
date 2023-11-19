package org.example;

import picocli.CommandLine;
import picocli.jansi.graalvm.AnsiConsole;

public class App {

	public static void main(String[] args) {
		int exitCode = 0;
		try (AnsiConsole ansi = AnsiConsole.windowsInstall()) {
			exitCode = new CommandLine(new CliPicocliApp()).execute(args);
		}
		System.exit(exitCode);
	}
}
