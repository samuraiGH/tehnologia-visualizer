package com.samurai.visualizer;

import com.samurai.visualizer.presenter.*;
import com.samurai.visualizer.model.*;
import com.samurai.visualizer.view.*;

public class Program {
	public static void main(String[] args) {
		var model = new BusinessModel();
		var view = new ConsoleView();
		
		var presenter = new Presenter(model, view);
		presenter.Start();
	}
}
