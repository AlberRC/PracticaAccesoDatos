package AccesoDatos.PracticaDatosEnFicherosTexto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class GestorUsuarios {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Qué fichero quieres modificar: ");
        String fichero = sc.nextLine();
        java.io.File archivo = new java.io.File(fichero);
        //Validación de tamaño máximo del fichero, siendo que el máximo es de 10000 bytes/10KB
        while (archivo.length() > 10000) {
            System.out.println("Error: El fichero excede el tamaño máximo de 10000 bytes, seleccione otro: ");
            fichero = sc.nextLine();
            archivo = new java.io.File(fichero);
        }
        int opcion = 0;

        //Menú principal
        while(opcion != 5) {
            System.out.print("""
                    ========== MENÚ PRINCIPAL ==========
                    1. Añadir usuario
                    2. Mostrar usuarios introducidos
                    3. Generar fichero de concordancias
                    4. Ordenar por ID
                    5. Salir
                    ==============================
                    Seleccione una opción:""");
            opcion = sc.nextInt();
            //Validación de opción del menú, para que no se puedan meter números que no harán nada
            while(opcion < 1 || opcion > 5) {
                System.out.print("Seleccione una opción válida: ");
                opcion = sc.nextInt();
            }
            switch(opcion) {
                case 1 -> anyadirUsuario(fichero);
                case 2 -> mostrarUsuarios(fichero);
                case 3 -> generarFicheroConcordancias(fichero);
                case 4 -> ordenarPorID(fichero);
                case 5 -> System.out.println("Vuelva pronto :)");
            }
        }
    }

    //Añade un nuevo usuario al fichero, ordena sus aficiones alfabéticamente, y valida que su ID sea único
    public static void anyadirUsuario(String fichero) {
        try {
            Scanner sc = new Scanner(System.in);
            FileWriter fileWriter = new FileWriter(fichero, true);
            int numeroRecomendado = calcularNumeroRecomendado(fichero);
            System.out.printf("Dime el ID de usuario(recomendado -> %d): ", numeroRecomendado);
            int IDusuario = sc.nextInt();
            boolean IDvalido = comprobarIDvalido(fichero, IDusuario);
            //Validar que el ID no exista/esté disponible
            while(!IDvalido) {
                System.out.printf("ID inválido, seleccione otro(recomendado -> %d): ", numeroRecomendado);
                IDusuario = sc.nextInt();
                IDvalido = comprobarIDvalido(fichero, IDusuario);
            }

            sc.nextLine(); //Limpiar buffer del scanner, sino no podría leer las aficiones
            System.out.print("Dime las aficiones separadas por espacios: ");
            String aficiones = sc.nextLine();
            //Validar que se introduzca al menos una afición
            while(aficiones.isEmpty()) {
                System.out.print("Mínimo una afición: ");
                aficiones = sc.nextLine();
            }
            String[] listaAficiones = aficiones.split(" ");

            //Convertir a mayúsculas y ordenar alfabéticamente
            for(int i = 0; i < listaAficiones.length; i++) {
                listaAficiones[i] = listaAficiones[i].toUpperCase();
            }
            Arrays.sort(listaAficiones);

            String aficionesUsuario = String.join(" ", listaAficiones);
            fileWriter.write("U" + IDusuario + " " + aficionesUsuario + "\n");
            System.out.println("Usuario registrado satisfactoriamente");
            fileWriter.close();
        } catch (java.io.IOException e) {
            System.out.println("Error: No se puede acceder al fichero " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: El ID debe ser un número entero");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    //Muestra todos los usuarios del fichero por consola
    public static void mostrarUsuarios(String fichero) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fichero));
            String linea;
            System.out.println("Leyendo usuarios...");
            while ((linea = bufferedReader.readLine()) != null) {
                System.out.println(linea);
            }
            bufferedReader.close();
        } catch (java.io.IOException e) {
            System.out.println("Error: No se puede leer el fichero " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    //Comprobar si un ID de usuario ya existe en el fichero
    public static boolean comprobarIDvalido(String fichero, int IDusuario) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fichero));
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                if (linea.isEmpty()) {
                    return true; //Si el fichero está vacío, el ID siempre estará disponible
                }
                String[] partesLinea = linea.split(" ");
                String IDusuarioActual = partesLinea[0];
                int IDusuarioNumero = Integer.parseInt(IDusuarioActual.substring(1));
                if (IDusuarioNumero == IDusuario) {
                    return false; //ID no disponible
                }
            }
            bufferedReader.close();
            return true; //ID disponible
        } catch (java.io.IOException e) {
            System.out.println("Error: No se puede leer el fichero " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            return false;
        }
    }

    //Calcula el siguiente ID disponible basado en el máximo ID que haya en el fichero
    public static int calcularNumeroRecomendado(String fichero) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fichero));
            String linea;
            int numeroRecomendado = 0;
            while ((linea = bufferedReader.readLine()) != null) {
                if (linea.isEmpty()) {
                    continue; //Para que no salte un error cuando el archivo esté creado pero vacío
                }
                String[] partesLinea = linea.split(" ");
                String IDusuario = partesLinea[0];
                int IDusuarioNumero = Integer.parseInt(IDusuario.substring(1));
                if(IDusuarioNumero > numeroRecomendado) {
                    numeroRecomendado = IDusuarioNumero;
                }
            }
            bufferedReader.close();
            return numeroRecomendado + 1;
        } catch (java.io.FileNotFoundException e) {
            System.out.println("El fichero no existe, empezando desde el ID 1");
            return 1;
        } catch (java.io.IOException e) {
            System.out.println("Error al leer el fichero, usando ID por defecto");
            return 1;
        } catch (Exception e) {
            System.out.println("Error inesperado, usando ID por defecto " + e.getMessage());
            return 1;
        }
    }

    //Ordena los usuarios del fichero por su ID, de menor a mayor
    public static void ordenarPorID(String fichero) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fichero));
            List<String> lineas = new ArrayList<>();
            String linea;
            //Leer todas las líneas del fichero
            while ((linea = bufferedReader.readLine()) != null) {
                lineas.add(linea);
            }
            bufferedReader.close();

            //Ordenar por ID numérico
            lineas.sort((l1, l2) -> {
                int id1 = Integer.parseInt(l1.split(" ")[0].substring(1));
                int id2 = Integer.parseInt(l2.split(" ")[0].substring(1));
                return Integer.compare(id1, id2);
            });

            //Reescribir el fichero ordenado
            FileWriter fileWriter = new FileWriter(fichero);
            for(String lineaIndividual : lineas) {
                fileWriter.write(lineaIndividual + "\n");
            }
            fileWriter.close();

            System.out.println("Fichero ordenado por ID satisfactoriamente");
        } catch (java.io.IOException e) {
            System.out.println("Error: No se puede acceder al fichero " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    //Genera un fichero con las parejas de usuarios que compartan aficiones en común
    public static void generarFicheroConcordancias(String fichero) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Cantidad mínima de aficiones en común: ");
            int cantidadMinimaAficiones = sc.nextInt();
            //Validar el mínimo de aficiones comunes
            while(cantidadMinimaAficiones < 1) {
                System.out.print("Mínimo una afición en común: ");
                cantidadMinimaAficiones = sc.nextInt();
            }

            //Leer usuarios y sus aficiones
            List<String> IDsUsuarios = new ArrayList<>();
            List<Set<String>> aficionesUsuarios = new ArrayList<>();

            BufferedReader bufferedReader = new BufferedReader(new FileReader(fichero));
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                String[] partesLinea = linea.split(" ");
                String IDusuario = partesLinea[0];
                Set<String> aficionesUsuario = new HashSet<>();

                //Almacenar aficiones en un Set, para operaciones junto a los IDs
                for(int i = 1; i < partesLinea.length; i++) {
                    aficionesUsuario.add(partesLinea[i].trim());
                }
                IDsUsuarios.add(IDusuario);
                aficionesUsuarios.add(aficionesUsuario);
            }
            bufferedReader.close();

            int cantidadParejas = 0;
            List<String> concordanciasEncontradas = new ArrayList<>();

            //Comparar todos los pares de usuarios posibles
            for(int i = 0; i < IDsUsuarios.size(); i++) {
                for(int j = i + 1; j < IDsUsuarios.size(); j++) {
                    String IDusuario1 = IDsUsuarios.get(i);
                    String IDusuario2 = IDsUsuarios.get(j);
                    Set<String> aficionesUsuario1 = aficionesUsuarios.get(i);
                    Set<String> aficionesUsuario2 = aficionesUsuarios.get(j);

                    //Encontrar aficiones comunes
                    Set<String> aficionesComunes = new HashSet<>(aficionesUsuario1);
                    aficionesComunes.retainAll(aficionesUsuario2);

                    //Si cumplen el mínimo de aficiones requerido, guardar la concordancia
                    if(aficionesComunes.size() >= cantidadMinimaAficiones) {
                        String aficionesComunesString = String.join(" ", aficionesComunes);
                        concordanciasEncontradas.add(IDusuario1 + " " + IDusuario2 + " " + aficionesComunesString);
                        cantidadParejas++;
                    }
                }
            }

            System.out.printf("Hay un total de %d parejas\n", cantidadParejas);

            //Crear fichero solo si hay concordancias
            if(cantidadParejas == 0) {
                System.out.println("Archivo 'concordancias.txt' no creado por la falta de concordancias");
            } else {
                FileWriter fileWriter = new FileWriter("concordancias.txt");
                for(String concordancia : concordanciasEncontradas) {
                    fileWriter.write(concordancia + "\n");
                }
                fileWriter.close();
                System.out.println("Archivo 'concordancias.txt' creado satisfactoriamente");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: La cantidad mínima de aficiones en común debe ser un número entero");
        } catch (java.io.IOException e) {
            System.out.println("Error: No se puede acceder a los ficheros " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
}