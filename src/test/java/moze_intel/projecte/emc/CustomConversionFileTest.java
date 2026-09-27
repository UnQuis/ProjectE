package moze_intel.projecte.emc;

import moze_intel.projecte.api.conversion.CustomConversionFile;
import moze_intel.projecte.impl.codec.CodecTestHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Проверяет, что файлы конверсий из датапака читаются кодеком ядра.
 * <p>
 * Файлы аддона пришли в его собственном формате, и ядро их молча отбрасывало: в логе оставалось только
 * «Failed to read conversions», из-за чего EMC считался исключительно по крафтам. Тест ломает сборку,
 * если файл не распарсился или в нём не осталось ни одной конверсии.
 */
@ExtendWith(EphemeralTestServerProvider.class)
@DisplayName("Check that the custom conversion files can be read")
class CustomConversionFileTest {

	/**
	 * Каталог с файлами конверсий аддона. Путь ищется от корня проекта, так как тесты запускаются с рабочим
	 * каталогом тест-раннера NeoForge.
	 */
	private static final Path CONVERSIONS = findProjectRoot().resolve("src/main/resources/data/projecte/pe_custom_conversions");

	private static Path findProjectRoot() {
		Path current = Path.of("").toAbsolutePath();
		while (current != null && !Files.exists(current.resolve("gradlew"))) {
			current = current.getParent();
		}
		Assertions.assertNotNull(current, "Не найден корень проекта");
		return current;
	}

	@Test
	@DisplayName("Файлы конверсий аддона читаются и содержат конверсии")
	void testExpansionConversionsAreReadable(MinecraftServer server) throws IOException {
		HolderLookup.Provider registryAccess = server.registryAccess();
		Assertions.assertTrue(Files.isDirectory(CONVERSIONS), "Нет каталога с конверсиями: " + CONVERSIONS);
		List<Path> files;
		try (Stream<Path> found = Files.list(CONVERSIONS)) {
			files = found.filter(path -> path.toString().endsWith(".json")).sorted().toList();
		}
		Assertions.assertFalse(files.isEmpty(), "Ни одного файла конверсий не найдено");

		int conversions = 0;
		for (Path file : files) {
			String rawJson = Files.readString(file);
			CustomConversionFile parsed = CodecTestHelper.parseJson(registryAccess, CustomConversionFile.CODEC, file.getFileName().toString(), rawJson);
			for (var group : parsed.groups().values()) {
				Assertions.assertFalse(group.conversions().isEmpty(), "В файле есть группа без конверсий: " + file);
				conversions += group.conversions().size();
			}
		}
		//Конверсии аддона задают EMC для топлива, материи, коллекторов, реле, сундуков и звёзд. Если их не считали,
		//у всех этих предметов остаётся только EMC по крафтам, поэтому число не должно быть пустым. Порог ниже
		//фактических 248, чтобы добавление нескольких конверсий не требовало правки теста
		Assertions.assertTrue(conversions >= 200, "Прочитано подозрительно мало конверсий: " + conversions);
	}
}
