import functions.*;
import functions.basic.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    
    private static void test1() {
        System.out.println("-".repeat(80));
        System.out.println("Тестирование работы всех написанных классов");
        System.out.println("-".repeat(80));
        
        testBasicFunctionsSinCos();
        testTabulatedAnalogues();
        testSumOfSquares();
        testFileOperationsExponential();
        testFileOperationsLogarithm();
        compareStorageFormats();
    }
    
    private static void test2() {
        System.out.println("=".repeat(80));
        System.out.println("ЗАДАНИЕ 9: ТЕСТИРОВАНИЕ СЕРИАЛИЗАЦИИ");
        System.out.println("=".repeat(80));
        
        testSerializableApproach();
        testExternalizableApproach();
        compareSerializationMethods();
    }
    
    private static void testBasicFunctionsSinCos() {
        System.out.println("\n1. Тестирование базовых функций Sin и Cos");
        System.out.println("-".repeat(50));
        
        Function sin = new Sin();
        Function cos = new Cos();
        
        double from = 0;
        double to = Math.PI;
        double step = 0.1;
        
        System.out.println("Значения sin(x) на [0, π] с шагом 0.1:");
        System.out.printf("%-8s %-10s%n", "x", "sin(x)");
        for (double x = from; x <= to + 1e-10; x += step) {
            System.out.printf("%-8.3f %-10.6f%n", x, sin.getFunctionValue(x));
        }
        
        System.out.println("\nЗначения cos(x) на [0, π] с шагом 0.1:");
        System.out.printf("%-8s %-10s%n", "x", "cos(x)");
        for (double x = from; x <= to + 1e-10; x += step) {
            System.out.printf("%-8.3f %-10.6f%n", x, cos.getFunctionValue(x));
        }
    }
    
    private static void testTabulatedAnalogues() {
        System.out.println("\n\n2. Табулированные аналоги Sin и Cos (10 точек)");
        System.out.println("-".repeat(55));
        
        Function sin = new Sin();
        Function cos = new Cos();
        
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        double from = 0;
        double to = Math.PI;
        double step = 0.1;
        
        System.out.println("Сравнение sin(x) и табулированного sin(x) (10 точек):");
        System.out.printf("%-8s %-12s %-12s %-12s%n", "x", "sin(x)", "tab_sin(x)", "погрешность");
        for (double x = from; x <= to + 1e-10; x += step) {
            double exact = sin.getFunctionValue(x);
            double approx = tabulatedSin.getFunctionValue(x);
            double error = Math.abs(exact - approx);
            System.out.printf("%-8.3f %-12.6f %-12.6f %-12.6f%n", x, exact, approx, error);
        }
        
        System.out.println("\nСравнение cos(x) и табулированного cos(x) (10 точек):");
        System.out.printf("%-8s %-12s %-12s %-12s%n", "x", "cos(x)", "tab_cos(x)", "погрешность");
        for (double x = from; x <= to + 1e-10; x += step) {
            double exact = cos.getFunctionValue(x);
            double approx = tabulatedCos.getFunctionValue(x);
            double error = Math.abs(exact - approx);
            System.out.printf("%-8.3f %-12.6f %-12.6f %-12.6f%n", x, exact, approx, error);
        }
    }
    
    private static void testSumOfSquares() {
        System.out.println("\n\n3. Сумма квадратов табулированных функций");
        System.out.println("-".repeat(45));
        
        int[] pointsCounts = {5, 10, 20};
        
        for (int pointsCount : pointsCounts) {
            System.out.println("\nКоличество точек в табулированных функциях: " + pointsCount);
            
            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, pointsCount);
            TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, pointsCount);
            
            Function sumOfSquares = Functions.sum(
                Functions.power(tabulatedSin, 2),
                Functions.power(tabulatedCos, 2)
            );
            
            double from = 0;
            double to = Math.PI;
            double step = 0.1;
            
            System.out.printf("%-8s %-15s%n", "x", "sin²(x)+cos²(x)");
            for (double x = from; x <= to + 1e-10; x += step) {
                double value = sumOfSquares.getFunctionValue(x);
                double deviation = Math.abs(value - 1.0);
                System.out.printf("%-8.3f %-15.8f (отклонение: %.8f)%n", x, value, deviation);
            }
        }
    }
    
    private static void testFileOperationsExponential() {
        System.out.println("\n\n4. Работа с текстовыми файлами (экспонента)");
        System.out.println("-".repeat(50));
        
        String filename = "exponential_function.txt";
        
        try {
            TabulatedFunction expFunction = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);
            
            try (FileWriter writer = new FileWriter(filename)) {
                TabulatedFunctions.writeTabulatedFunction(expFunction, writer);
            }
            System.out.println("Табулированная экспонента записана в файл: " + filename);
            
            TabulatedFunction readFunction;
            try (FileReader reader = new FileReader(filename)) {
                readFunction = TabulatedFunctions.readTabulatedFunction(reader);
            }
            System.out.println("Функция прочитана из текстового файла");
            
            System.out.println("\nСравнение исходной и прочитанной функции:");
            System.out.printf("%-8s %-15s %-15s %-15s%n", "x", "исходная", "прочитанная", "разница");
            for (int i = 0; i < expFunction.getPointsCount(); i++) {
                double x = expFunction.getPointX(i);
                double original = expFunction.getPointY(i);
                double read = readFunction.getPointY(i);
                double difference = Math.abs(original - read);
                System.out.printf("%-8.1f %-15.8f %-15.8f %-15.8f%n", x, original, read, difference);
            }
            
            System.out.println("\nСодержимое текстового файла:");
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            System.out.println(content);
            
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("Временный файл удален");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void testFileOperationsLogarithm() {
        System.out.println("\n\n5. Работа с бинарными файлами (логарифм)");
        System.out.println("-".repeat(50));
        
        String filename = "logarithm_function.dat";
        
        try {
            TabulatedFunction logFunction = TabulatedFunctions.tabulate(new Log(Math.E), 1, 10, 11);
            
            try (FileOutputStream out = new FileOutputStream(filename)) {
                TabulatedFunctions.outputTabulatedFunction(logFunction, out);
            }
            System.out.println("Табулированный логарифм записан в файл: " + filename);
            
            TabulatedFunction readFunction;
            try (FileInputStream in = new FileInputStream(filename)) {
                readFunction = TabulatedFunctions.inputTabulatedFunction(in);
            }
            System.out.println("Функция прочитана из бинарного файла");
            
            System.out.println("\nСравнение исходной и прочитанной функции:");
            System.out.printf("%-8s %-15s %-15s %-15s%n", "x", "исходная", "прочитанная", "разница");
            for (int i = 0; i < logFunction.getPointsCount(); i++) {
                double x = logFunction.getPointX(i);
                double original = logFunction.getPointY(i);
                double read = readFunction.getPointY(i);
                double difference = Math.abs(original - read);
                System.out.printf("%-8.1f %-15.8f %-15.8f %-15.8f%n", x, original, read, difference);
            }
            
            File file = new File(filename);
            System.out.println("\nРазмер бинарного файла: " + file.length() + " байт");
            
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("Временный файл удален");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void compareStorageFormats() {
        System.out.println("\n\n6. Сравнение орматов хранения");
        System.out.println("-".repeat(35));
        
        String textFile = "comparison_text.txt";
        String binaryFile = "comparison_binary.dat";
        
        try {
            TabulatedFunction testFunction = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 5);
            
            try (FileWriter writer = new FileWriter(textFile)) {
                TabulatedFunctions.writeTabulatedFunction(testFunction, writer);
            }
            
            try (FileOutputStream out = new FileOutputStream(binaryFile)) {
                TabulatedFunctions.outputTabulatedFunction(testFunction, out);
            }
            
            File text = new File(textFile);
            File binary = new File(binaryFile);
            
            System.out.println("Размер текстового файла: " + text.length() + " байт");
            System.out.println("Размер бинарного файла: " + binary.length() + " байт");
            System.out.println("Бинарный файл занимает " + 
                String.format("%.1f", (double)binary.length() / text.length() * 100) + "% от текстового");
            
            System.out.println("\nСодержимое текстового файла:");
            System.out.println(new String(Files.readAllBytes(Paths.get(textFile))));
            
            Files.deleteIfExists(Paths.get(textFile));
            Files.deleteIfExists(Paths.get(binaryFile));
            System.out.println("\nВременные файлы удалены");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void testSerializableApproach() {
    System.out.println("\n1. СЕРИАЛИЗАЦИЯ ЧЕРЕЗ Serializable");
    System.out.println("=".repeat(45));
    
    String filename = "serializable_function.ser";
    
    try {
        // Создаем функцию, которая использует ТОЛЬКО Serializable
        TabulatedFunction originalFunction = createSerializableOnlyFunction();
        
        System.out.println("Функция создана:");
        System.out.println("  Тип: " + originalFunction.getClass().getSimpleName());
        System.out.println("  Количество точек: " + originalFunction.getPointsCount());
        System.out.println("  Область определения: [" + originalFunction.getLeftDomainBorder() + ", " + originalFunction.getRightDomainBorder() + "]");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(originalFunction);
        }
        System.out.println("✓ Сериализована в файл: " + filename);
        
        TabulatedFunction deserializedFunction;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFunction = (TabulatedFunction) in.readObject();
        }
        System.out.println("✓ Десериализована из файла");
        System.out.println("  Тип после десериализации: " + deserializedFunction.getClass().getSimpleName());
        
        System.out.println("\nСравнение исходной и десериализованной функции:");
        System.out.printf("%-8s %-12s %-12s %-10s%n", "x", "исходная", "десериал.", "разница");
        boolean allMatch = true;
        for (int i = 0; i < originalFunction.getPointsCount(); i++) {
            double x = originalFunction.getPointX(i);
            double original = originalFunction.getPointY(i);
            double deserialized = deserializedFunction.getPointY(i);
            double diff = Math.abs(original - deserialized);
            System.out.printf("%-8.1f %-12.6f %-12.6f %-10.6f", x, original, deserialized, diff);
            if (diff > 1e-10) {
                System.out.print(" ✗");
                allMatch = false;
            } else {
                System.out.print(" ✓");
            }
            System.out.println();
        }
        
        File file = new File(filename);
        System.out.println("\nРазмер файла Serializable: " + file.length() + " байт");
        
        Files.deleteIfExists(Paths.get(filename));
        System.out.println("✓ Временный файл удален");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}

private static void testExternalizableApproach() {
    System.out.println("\n\n2. СЕРИАЛИЗАЦИЯ ЧЕРЕЗ Externalizable");
    System.out.println("=".repeat(50));
    
    String filename = "externalizable_function.ser";
    
    try {
        // Создаем функцию, которая использует Externalizable
        TabulatedFunction originalFunction = createExternalizableFunction();
        
        System.out.println("Функция создана:");
        System.out.println("  Тип: " + originalFunction.getClass().getSimpleName());
        System.out.println("  Количество точек: " + originalFunction.getPointsCount());
        System.out.println("  Область определения: [" + originalFunction.getLeftDomainBorder() + ", " + originalFunction.getRightDomainBorder() + "]");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(originalFunction);
        }
        System.out.println("✓ Сериализована в файл: " + filename);
        
        TabulatedFunction deserializedFunction;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFunction = (TabulatedFunction) in.readObject();
        }
        System.out.println("✓ Десериализована из файла");
        System.out.println("  Тип после десериализации: " + deserializedFunction.getClass().getSimpleName());
        
        System.out.println("\nСравнение исходной и десериализованной функции:");
        System.out.printf("%-8s %-12s %-12s %-10s%n", "x", "исходная", "десериал.", "разница");
        boolean allMatch = true;
        for (int i = 0; i < originalFunction.getPointsCount(); i++) {
            double x = originalFunction.getPointX(i);
            double original = originalFunction.getPointY(i);
            double deserialized = deserializedFunction.getPointY(i);
            double diff = Math.abs(original - deserialized);
            System.out.printf("%-8.1f %-12.6f %-12.6f %-10.6f", x, original, deserialized, diff);
            if (diff > 1e-10) {
                System.out.print(" ✗");
                allMatch = false;
            } else {
                System.out.print(" ✓");
            }
            System.out.println();
        }
        
        File file = new File(filename);
        System.out.println("\nРазмер файла Externalizable: " + file.length() + " байт");
        
        Files.deleteIfExists(Paths.get(filename));
        System.out.println("✓ Временный файл удален");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}

// Функция, которая использует ТОЛЬКО Serializable (не реализует Externalizable)
private static TabulatedFunction createSerializableOnlyFunction() {
    // Создаем LinkedListTabulatedFunction через массив FunctionPoint
    FunctionPoint[] points = new FunctionPoint[11];
    Function composition = Functions.composition(new Exp(), new Log(Math.E));
    
    for (int i = 0; i < 11; i++) {
        double x = i;
        double y = composition.getFunctionValue(x);
        points[i] = new FunctionPoint(x, y);
    }
    
    return new LinkedListTabulatedFunction(points);
}

// Функция, которая использует Externalizable
private static TabulatedFunction createExternalizableFunction() {
    // Создаем ArrayTabulatedFunction через два массива
    double[] xValues = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    double[] yValues = new double[11];
    Function composition = Functions.composition(new Exp(), new Log(Math.E));
    
    for (int i = 0; i < 11; i++) {
        yValues[i] = composition.getFunctionValue(xValues[i]);
    }
    
    return new ArrayTabulatedFunction(xValues, yValues);
}

private static void compareSerializationMethods() {
    System.out.println("\n\n3. СРАВНЕНИЕ МЕТОДОВ СЕРИАЛИЗАЦИИ");
    System.out.println("=".repeat(45));
    
    String serializableFile = "comparison_serializable.ser";
    String externalizableFile = "comparison_externalizable.ser";
    
    try {
        // Serializable-only функция (LinkedListTabulatedFunction)
        TabulatedFunction serializableFunc = createSerializableOnlyFunction();
        // Externalizable функция (ArrayTabulatedFunction)
        TabulatedFunction externalizableFunc = createExternalizableFunction();
        
        // Сериализация через стандартный Serializable
        try (ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(serializableFile))) {
            out1.writeObject(serializableFunc);
        }
        
        // Сериализация через Externalizable
        try (ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
            out2.writeObject(externalizableFunc);
        }
        
        File serializable = new File(serializableFile);
        File externalizable = new File(externalizableFile);
        
        long serializableSize = serializable.length();
        long externalizableSize = externalizable.length();
        long difference = Math.abs(serializableSize - externalizableSize);
        double percent = (1 - (double)externalizableSize / serializableSize) * 100;
        
        System.out.println("Размеры файлов:");
        System.out.println("  Serializable (LinkedList): " + serializableSize + " байт");
        System.out.println("  Externalizable (Array): " + externalizableSize + " байт");
        System.out.println("  Разница: " + difference + " байт");
        System.out.printf("  Экономия: %.1f%%\n", Math.abs(percent));
        
        System.out.println("\nАнализ подходов:");
        System.out.println("  Serializable: автоматическая сериализация всех полей");
        System.out.println("  Externalizable: ручное управление, только нужные данные");
        
        Files.deleteIfExists(Paths.get(serializableFile));
        Files.deleteIfExists(Paths.get(externalizableFile));
        System.out.println("\n✓ Временные файлы удалены");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    public static void main(String[] args) {
        test1();
        test2();
    }
}