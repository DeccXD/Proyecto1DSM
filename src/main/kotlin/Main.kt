import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    crearCarpetaLogs()
    val productos = crearInventario()
    pantallaInicio(productos)
}

private fun crearCarpetaLogs() {
    try {
        val dir = File("logs")
        if (!dir.exists()) dir.mkdirs()
        val log = File(dir, "errores.log")
        if (!log.exists()) log.createNewFile()
    } catch (e: Exception) {
        println("No se pudo crear carpeta de logs: ${e.message}")
    }
}

private fun logError(mensaje: String) {
    try {
        val archivo = File("logs/errores.log")
        val ahora = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        archivo.appendText("[$ahora] $mensaje\n")
    } catch (e: Exception) {
        println("Error escribiendo log: ${e.message}")
    }
}

private fun pantallaInicio(productos: MutableList<Producto>) {
    val carrito = Carrito()

    while (true) {
        try {
            println("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*")
            println("*                                         *")
            println("*      BIENVENIDO/A A LA PASTELERÍA       *")
            println("*                                         *")
            println("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*")
            println()
            println("Presiona:")
            println("  1) Ver menú de pasteles")
            println("  2) Ver carrito")
            println("  3) Salir")
            print("Opción: ")

            when (readLine()?.trim()) {
                "1" -> {
                    agregarAlCarrito(productos, carrito)
                    println("\nPresiona ENTER para volver al inicio...")
                    readLine()
                }
                "2" -> {
                    carrito.mostrarCarrito()
                    println("\nPresiona ENTER para volver al inicio...")
                    readLine()
                }
                "3" -> {
                    println("¡Gracias por visitar la Pastelería! ")
                    return
                }
                else -> {
                    println("❌ Opción inválida. Elige 1, 2 o 3.")
                }
            }
        } catch (e: Exception) {
            println("Ocurrió un error inesperado.")
            logError("pantallaInicio: ${e.message}")
        }
    }
}

private fun mostrarMenu(productos: MutableList<Producto>) {
    println("\n----- MENÚ DE PASTELES -----")
    println(String.format("%-3s %-30s %-10s %-6s", "ID", "Nombre", "Precio", "Stock"))
    println("-------------------------------------------------------------")
    for (p in productos) {
        println(
            String.format(
                "%-3d %-30s %-10s %-6d",
                p.id,
                p.nombre,
                "$${"%.2f".format(p.precio)}",
                p.cantidadDisponible
            )
        )
    }
}

private fun crearInventario(): MutableList<Producto> {
    return mutableListOf(
        Producto(1, "Pastel de Chocolate", 24.99, 7),
        Producto(2, "Pastel de Vainilla", 24.99, 9),
        Producto(3, "Pastel de Fresa", 24.99, 6),
        Producto(4, "Pastel de Frutas", 19.99, 15),
        Producto(5, "Pastel de Cumpleaños", 19.99, 25),
        Producto(6, "Pastel de Bodas", 49.99, 5),
        Producto(7, "Pie de Higo", 9.99, 10),
        Producto(8, "Pie de Manzana", 9.99, 15),
        Producto(9, "Docena de Cupcakes", 14.99, 10),
        Producto(10, "Docena de Galletas", 9.99, 10)
    )
}

private fun agregarAlCarrito(productos: MutableList<Producto>, carrito: Carrito) {
    while (true) {
        mostrarMenu(productos)
        print("\nIngresa el ID del pastel que quieres comprar (o 0 para volver al inicio): ")
        val id = readLine()?.toIntOrNull()

        if (id == null) {
            println("ERROR Ingresa un número válido.")
            continue
        }

        if (id == 0) {
            println(" Volviendo al menú principal...")
            return
        }

        val producto = productos.find { it.id == id }
        if (producto == null) {
            println("ERROR El pastel con ID $id no existe.")
            continue
        }

        while (true) {
            print("¿Cuántas unidades quieres de '${producto.nombre}'?: ")
            val cantidad = readLine()?.toIntOrNull()

            if (cantidad == null || cantidad <= 0) {
                println("ERROR Ingresa un número entero positivo.")
                continue
            }

            if (cantidad > producto.cantidadDisponible) {
                println("Lo sentimos no hay suficiente stock. Solo quedan ${producto.cantidadDisponible}.")
                continue
            }

            producto.cantidadDisponible -= cantidad
            carrito.agregarProducto(producto, cantidad)

            println("Se agregaron $cantidad '${producto.nombre}' al carrito.")
            break
        }
    }
}
