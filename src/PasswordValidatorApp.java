import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Aplicación de consola simple para validar contraseñas de usuario de manera concurrente.
 * Utiliza expresiones regulares para verificar si una contraseña cumple con criterios
 * específicos y procesa la validación en hilos separados utilizando un ExecutorService.
 */
public class PasswordValidatorApp {

    // Patrones de expresiones regulares para los criterios de validación de contraseña.
    // Verifica si la contraseña tiene al menos 8 caracteres de longitud.
    static final Pattern LENGTH = Pattern.compile(".{8,}");
    // Verifica si la contraseña contiene al menos un carácter especial (no es una letra o dígito).
    static final Pattern SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");
    // Verifica si la contraseña contiene al menos dos letras mayúsculas.
    static final Pattern UPPER = Pattern.compile(".*[A-Z].*[A-Z].*");
    // Verifica si la contraseña contiene al menos tres letras minúsculas.
    static final Pattern LOWER = Pattern.compile(".*[a-z].*[a-z].*[a-z].*");
    // Verifica si la contraseña contiene al menos un dígito.
    static final Pattern DIGIT = Pattern.compile(".*\\d.*");

    /**
     * Método principal para ejecutar la aplicación de validación de contraseñas.
     * Inicializa un Scanner para la entrada del usuario y un ExecutorService para la validación concurrente.
     * Solicita continuamente al usuario contraseñas para validar hasta que se escribe 'exit'.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        // Inicializa un objeto Scanner para leer la entrada del usuario desde la consola.
        Scanner scanner = new Scanner(System.in);
        // Crea un pool de hilos fijo para ejecutar tareas de validación de forma concurrente.
        ExecutorService executor = Executors.newFixedThreadPool(4);

        System.out.println("\n=== Validador de Contraseñas ===");
        System.out.println("Escribe 'exit' para terminar.\n");

        // Bucle principal para leer contraseñas del usuario.
        while (true) {
            System.out.print("Ingresa la contraseña a validar: ");
            String input = scanner.nextLine();
            // Sale del bucle si el usuario escribe 'exit' (sin distinguir mayúsculas/minúsculas).
            if (input.equalsIgnoreCase("exit")) break;

            // Envía la tarea de validación al servicio de ejecución para su ejecución asíncrona.
            // La variable input es efectivamente final aquí.
            executor.execute(() -> validate(input));
        }

        // Apaga el servicio de ejecución, permitiendo que las tareas actualmente en ejecución se completen.
        executor.shutdown();
        // Cierra el recurso Scanner para prevenir fugas de recursos.
        scanner.close();
    }

    /**
     * Valida una contraseña dada contra criterios predefinidos utilizando patrones de expresiones regulares.
     * Imprime si la contraseña es válida o inválida, y proporciona retroalimentación sobre
     * qué criterios fallaron si es inválida.
     * @param pass La cadena de la contraseña a validar.
     */
    static void validate(String pass) {
        boolean isValid = true;
        StringBuilder feedback = new StringBuilder();

        // Verifica cada criterio de validación y añade retroalimentación si falla.
        if (!LENGTH.matcher(pass).matches()) {
            isValid = false;
            feedback.append(" - Debe tener al menos 8 caracteres de longitud.\n");
        }
        if (!SPECIAL.matcher(pass).matches()) {
            isValid = false;
            feedback.append(" - Debe contener al menos un carácter especial.\n");
        }
        if (!UPPER.matcher(pass).matches()) {
            isValid = false;
            feedback.append(" - Debe contener al menos dos letras mayúsculas.\n");
        }
        if (!LOWER.matcher(pass).matches()) {
            isValid = false;
            feedback.append(" - Debe contener al menos tres letras minúsculas.\n");
        }
        if (!DIGIT.matcher(pass).matches()) {
            isValid = false;
            feedback.append(" - Debe contener al menos un dígito.\n");
        }

        // Determina el estado general de la validación.
        String resultStatus = isValid ? "✔ VÁLIDA" : "✖ INVÁLIDA";
        // Imprime el estado de la validación y la contraseña.
        System.out.printf(" [%s] \"%s\"", resultStatus, pass);

        // Si la contraseña es inválida, imprime la retroalimentación detallada.
        if (!isValid) {
            System.out.println("\n   La validación falló:");
            System.out.print(feedback.toString()); // Usa print en lugar de println para evitar una nueva línea extra.
        } else {
            System.out.println(); // Imprime una nueva línea para contraseñas válidas para un espaciado de salida consistente.
        }
    }
}