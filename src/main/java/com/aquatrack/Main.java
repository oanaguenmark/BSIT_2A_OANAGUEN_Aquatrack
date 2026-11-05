package com.aquatrack;

import com.aquatrack.dao.DatabaseInitializer;
import com.aquatrack.dao.UserDao;
import com.aquatrack.service.RefillStationSystem;
import com.aquatrack.ui.Authentication;

import java.util.Scanner;

/** Application entry point: init database -> create shared objects -> start Authentication. */
public class Main {

    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        UserDao userDao = new UserDao();
        RefillStationSystem system = new RefillStationSystem();
        Scanner scanner = new Scanner(System.in);

        new Authentication(userDao, scanner, system).start();

        scanner.close();
    }
}
