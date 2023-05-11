-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 01-12-2019 a las 07:45:18
-- Versión del servidor: 10.4.8-MariaDB
-- Versión de PHP: 7.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `dbdinora`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria`
--

CREATE TABLE `categoria` (
  `idcategoria` int(11) NOT NULL,
  `nombre` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `inactiva` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `categoria`
--

INSERT INTO `categoria` (`idcategoria`, `nombre`, `inactiva`) VALUES
(1, 'cocina', 0),
(2, 'Juego de sala', 0),
(3, 'Prueba', 0),
(4, 'Prueba2', 0),
(5, 'categoria1', 0),
(6, 'categoria2', 0),
(7, 'categoria3', 0),
(8, 'OTRA', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `idcliente` int(11) NOT NULL,
  `nombre` varchar(45) COLLATE utf8_spanish_ci NOT NULL,
  `direccion` varchar(45) COLLATE utf8_spanish_ci NOT NULL,
  `telefono` varchar(11) COLLATE utf8_spanish_ci NOT NULL,
  `limiteCredito` decimal(20,2) NOT NULL,
  `inactivo` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`idcliente`, `nombre`, `direccion`, `telefono`, `limiteCredito`, `inactivo`) VALUES
(1, 'Al contado', '----', '----', '0.00', 0),
(2, 'DANIEL HERNÁNDEZ DÍAZ', 'SAN PEDRO N.', '9191346604', '500000000.00', 0),
(3, 'DAVID A HERNÁNDEZ', 'SAN PEDRO N.', '9191236512', '20000.00', 0),
(4, 'DOMINGO HERNÁNDEZ', 'SAN PEDRO N.', '9191239045', '100000.00', 0),
(5, 'MARTIN DIAZ', 'AJILO', '9193451290', '5000.00', 0),
(6, 'ANTONIO DIAZ', 'CARRANZA MPIO BOCHIL', '9192347834', '50000.00', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `configuracion`
--

CREATE TABLE `configuracion` (
  `idconfiguracion` int(11) NOT NULL,
  `folio` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `ruta_logo` varchar(1000) COLLATE utf8_spanish_ci NOT NULL,
  `str_ticket` varchar(500) COLLATE utf8_spanish_ci NOT NULL,
  `impuesto` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `nombre_impresora` varchar(60) COLLATE utf8_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `configuracion`
--

INSERT INTO `configuracion` (`idconfiguracion`, `folio`, `ruta_logo`, `str_ticket`, `impuesto`, `nombre_impresora`) VALUES
(1, 'FOLIO:', 'logo_muebleria_4_header.png', 'MUEBLERIA DINORA/1DA AV. SUR PONIENTE NO. 56/BARRIO MORELOS/BOCHIL,CHIAPAS/(919) 653 2 87/GRACIAS POR SU COMPRA', 'TRUE/016', 'Microsoft Print to PDF');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalleticket`
--

CREATE TABLE `detalleticket` (
  `iddetalleticket` int(11) NOT NULL,
  `idticket` int(11) NOT NULL,
  `idproducto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precioVenta` decimal(7,2) NOT NULL,
  `devuelto` tinyint(4) DEFAULT 0,
  `cantidadDevo` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `detalleticket`
--

INSERT INTO `detalleticket` (`iddetalleticket`, `idticket`, `idproducto`, `cantidad`, `precioVenta`, `devuelto`, `cantidadDevo`) VALUES
(1, 1, 14, 1, '9.00', 0, 0),
(2, 2, 14, 2, '9.00', 0, 0),
(3, 2, 2, 3, '4000.00', 0, 0),
(4, 2, 3, 1, '100.00', 0, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

CREATE TABLE `empleado` (
  `idempleado` int(11) NOT NULL,
  `nombre` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `usuario` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `password` varbinary(100) NOT NULL,
  `email` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `privilegio` int(1) NOT NULL,
  `inactivo` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `empleado`
--

INSERT INTO `empleado` (`idempleado`, `nombre`, `usuario`, `password`, `email`, `privilegio`, `inactivo`) VALUES
(1, 'Administrador', '12345678', 0x346d6c58636452732b5a367775707750745545324e413d3d, 'danielhd94@hotmail.com', 1, 0),
(4, 'RECUPERACION', 'admin', 0x346d6c58636452732b5a367775707750745545324e413d3d, 'recuperacion@hotmail.com', 1, 0),
(6, 'Administrador', '12345', 0x346d6c58636452732b5a367775707750745545324e413d3d, 'danielhd94@hotmail.com', 1, 1),
(7, 'Daniel', 'daniel', 0x4c6a76796f555258576b2b4335776e436274717a33773d3d, 'daniel@hotmail.com', 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `entradasalida`
--

CREATE TABLE `entradasalida` (
  `identsal` int(11) NOT NULL,
  `idempleado` int(11) NOT NULL,
  `monto` decimal(7,2) NOT NULL,
  `descripcion` varchar(45) COLLATE utf8_spanish_ci DEFAULT NULL,
  `tipo` int(1) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inventario`
--

CREATE TABLE `inventario` (
  `idinvetario` int(11) NOT NULL,
  `idproducto` int(11) NOT NULL,
  `idempleado` int(11) NOT NULL,
  `habia` int(11) NOT NULL,
  `tipomovimiento` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `inventario`
--

INSERT INTO `inventario` (`idinvetario`, `idproducto`, `idempleado`, `habia`, `tipomovimiento`, `cantidad`, `fecha`) VALUES
(1, 14, 1, 181, 2, 2, '2019-12-01 06:39:03'),
(2, 2, 1, 15, 2, 3, '2019-12-01 06:39:03'),
(3, 3, 1, 93, 2, 1, '2019-12-01 06:39:04');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `movimientocliente`
--

CREATE TABLE `movimientocliente` (
  `idmovimientocliente` int(11) NOT NULL,
  `idcliente` int(11) NOT NULL,
  `tipo` int(11) NOT NULL,
  `monto` double NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `movimientocliente`
--

INSERT INTO `movimientocliente` (`idmovimientocliente`, `idcliente`, `tipo`, `monto`, `fecha`) VALUES
(1, 6, 0, 10.44, '2019-02-04 18:11:47');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

CREATE TABLE `pago` (
  `idpago` int(11) NOT NULL,
  `idcliente` int(11) NOT NULL,
  `idempleado` int(11) NOT NULL,
  `monto` decimal(7,2) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `idproducto` int(11) NOT NULL,
  `idcategoria` int(11) NOT NULL,
  `noserie` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `descripcion` varchar(45) COLLATE utf8_spanish_ci NOT NULL,
  `precioCosto` decimal(7,2) NOT NULL,
  `precioVenta` decimal(7,2) NOT NULL,
  `precioMayoreo` decimal(7,2) NOT NULL,
  `existencia` int(11) NOT NULL,
  `cantidadMinima` int(11) NOT NULL,
  `inactivo` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`idproducto`, `idcategoria`, `noserie`, `descripcion`, `precioCosto`, `precioVenta`, `precioMayoreo`, `existencia`, `cantidadMinima`, `inactivo`) VALUES
(1, 1, '01', 'ESTUFA', '120.00', '200.00', '150.00', 77, 5, 0),
(2, 2, '02', 'SILLON GRANDE', '3500.00', '4000.00', '3800.00', 12, 10, 0),
(3, 2, '03', 'PRUEBA', '100.00', '100.00', '100.00', 92, 100, 0),
(6, 3, '654362', 'Producto de Prueba', '10.00', '15.00', '13.00', 243, 3, 0),
(7, 3, '129834', 'Otro producto de prueba', '9.00', '20.00', '16.00', 22, 4, 0),
(8, 7, '655363', 'producto03', '78.00', '455.00', '345.00', 37, 34, 0),
(9, 5, '533534', 'producto1', '10.00', '30000.00', '13.00', 3415, 3, 0),
(10, 6, '779834', 'producto02', '9.00', '20.00', '16.00', 24, 4, 0),
(11, 3, '232141', 'producto04', '10.00', '15.00', '13.00', 57, 3, 0),
(12, 3, '786788', 'producto05', '9.00', '20.00', '16.00', 1, 4, 0),
(13, 4, '353535', 'producto06', '78.00', '455.00', '345.00', 24, 34, 0),
(14, 1, '01', 'DESC101', '9.00', '9.00', '9.00', 179, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `promocion`
--

CREATE TABLE `promocion` (
  `idpromocion` int(11) NOT NULL,
  `idproducto` int(11) NOT NULL,
  `nombre` varchar(45) COLLATE utf8_spanish_ci NOT NULL,
  `desde` int(11) NOT NULL,
  `hasta` int(11) NOT NULL,
  `preciopromocion` decimal(7,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ticket`
--

CREATE TABLE `ticket` (
  `idticket` int(11) NOT NULL,
  `idempleado` int(11) NOT NULL,
  `idcliente` int(11) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `iva` int(11) NOT NULL,
  `pagoCon` decimal(7,2) NOT NULL,
  `estado` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `ticket`
--

INSERT INTO `ticket` (`idticket`, `idempleado`, `idcliente`, `fecha`, `iva`, `pagoCon`, `estado`) VALUES
(1, 1, 6, '2019-02-04 18:11:47', 16, '0.00', 0),
(2, 1, 1, '2019-12-01 06:39:03', 16, '14056.88', 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categoria`
--
ALTER TABLE `categoria`
  ADD PRIMARY KEY (`idcategoria`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`idcliente`);

--
-- Indices de la tabla `configuracion`
--
ALTER TABLE `configuracion`
  ADD PRIMARY KEY (`idconfiguracion`);

--
-- Indices de la tabla `detalleticket`
--
ALTER TABLE `detalleticket`
  ADD PRIMARY KEY (`iddetalleticket`,`idticket`,`idproducto`),
  ADD KEY `fk_ventas_has_productos_productos1_idx` (`idproducto`),
  ADD KEY `fk_ventas_has_productos_ventas1_idx` (`idticket`);

--
-- Indices de la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD PRIMARY KEY (`idempleado`);

--
-- Indices de la tabla `entradasalida`
--
ALTER TABLE `entradasalida`
  ADD PRIMARY KEY (`identsal`),
  ADD KEY `fk_salidas_empleado_idx` (`idempleado`);

--
-- Indices de la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD PRIMARY KEY (`idinvetario`),
  ADD KEY `fk_inventario_producctor_idx` (`idproducto`),
  ADD KEY `fk_inventario_empleado_idx` (`idempleado`);

--
-- Indices de la tabla `movimientocliente`
--
ALTER TABLE `movimientocliente`
  ADD PRIMARY KEY (`idmovimientocliente`),
  ADD KEY `fk_cliente_movimientocliente` (`idcliente`);

--
-- Indices de la tabla `pago`
--
ALTER TABLE `pago`
  ADD PRIMARY KEY (`idpago`),
  ADD KEY `fk_pagos_cliente_idx` (`idcliente`),
  ADD KEY `fk_pagos_empleado_idx` (`idempleado`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`idproducto`),
  ADD KEY `fk_productos_categoria_idx` (`idcategoria`);

--
-- Indices de la tabla `promocion`
--
ALTER TABLE `promocion`
  ADD PRIMARY KEY (`idpromocion`),
  ADD KEY `fk_promociones_productos_idx` (`idproducto`);

--
-- Indices de la tabla `ticket`
--
ALTER TABLE `ticket`
  ADD PRIMARY KEY (`idticket`),
  ADD KEY `fk_ventas_empleado_idx` (`idempleado`),
  ADD KEY `fk_ventas_cliente_idx` (`idcliente`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categoria`
--
ALTER TABLE `categoria`
  MODIFY `idcategoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `idcliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `configuracion`
--
ALTER TABLE `configuracion`
  MODIFY `idconfiguracion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `detalleticket`
--
ALTER TABLE `detalleticket`
  MODIFY `iddetalleticket` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `empleado`
--
ALTER TABLE `empleado`
  MODIFY `idempleado` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `entradasalida`
--
ALTER TABLE `entradasalida`
  MODIFY `identsal` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `inventario`
--
ALTER TABLE `inventario`
  MODIFY `idinvetario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `movimientocliente`
--
ALTER TABLE `movimientocliente`
  MODIFY `idmovimientocliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `idpago` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `idproducto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `promocion`
--
ALTER TABLE `promocion`
  MODIFY `idpromocion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ticket`
--
ALTER TABLE `ticket`
  MODIFY `idticket` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalleticket`
--
ALTER TABLE `detalleticket`
  ADD CONSTRAINT `fk_producto_ticket` FOREIGN KEY (`idticket`) REFERENCES `ticket` (`idticket`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_ticket_producto` FOREIGN KEY (`idproducto`) REFERENCES `producto` (`idproducto`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Filtros para la tabla `entradasalida`
--
ALTER TABLE `entradasalida`
  ADD CONSTRAINT `fk_entradasalida_empleado` FOREIGN KEY (`idempleado`) REFERENCES `empleado` (`idempleado`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Filtros para la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD CONSTRAINT `fk_inventario_empleado` FOREIGN KEY (`idempleado`) REFERENCES `empleado` (`idempleado`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_inventario_producto` FOREIGN KEY (`idproducto`) REFERENCES `producto` (`idproducto`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Filtros para la tabla `movimientocliente`
--
ALTER TABLE `movimientocliente`
  ADD CONSTRAINT `fk_cliente_movimientocliente` FOREIGN KEY (`idcliente`) REFERENCES `cliente` (`idcliente`);

--
-- Filtros para la tabla `pago`
--
ALTER TABLE `pago`
  ADD CONSTRAINT `fk_pago_cliente` FOREIGN KEY (`idcliente`) REFERENCES `cliente` (`idcliente`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_pago_empleado` FOREIGN KEY (`idempleado`) REFERENCES `empleado` (`idempleado`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`idcategoria`) REFERENCES `categoria` (`idcategoria`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Filtros para la tabla `promocion`
--
ALTER TABLE `promocion`
  ADD CONSTRAINT `fk_promocion_producto` FOREIGN KEY (`idproducto`) REFERENCES `producto` (`idproducto`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `ticket`
--
ALTER TABLE `ticket`
  ADD CONSTRAINT `fk_ticket_cliente` FOREIGN KEY (`idcliente`) REFERENCES `cliente` (`idcliente`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_ticket_empleado` FOREIGN KEY (`idempleado`) REFERENCES `empleado` (`idempleado`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
