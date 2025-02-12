import os

# Функция для записи файлов .kt и .xml в txt
def collect_files(directory, file_extension, txt_file):
    # Пройтись по всем файлам и подкаталогам в директории
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(file_extension):
                file_path = os.path.join(root, file)
                print(f"Обрабатываем файл: {file_path}")  # Диагностика
                txt_file.write(f'{file}\n')  # Записать название файла
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    txt_file.write(f.read())  # Записать содержимое файла
                txt_file.write('\n\n')  # Добавить разделитель

# Путь к проекту (предполагаем, что скрипт запускается в корне проекта HowTo)
project_dir = os.getcwd()

# Путь к папке com/blazik/howto для .kt файлов
howto_dir = os.path.join(project_dir, 'app', 'src', 'main', 'java', 'com', 'blazik', 'howto')
# Путь к папке res/layout для .xml файлов
layout_dir = os.path.join(project_dir, 'app', 'src', 'main', 'res')

# Открыть файл для записи
with open('collected_code.txt', 'w', encoding='utf-8') as txt_file:
    # Собираем .kt файлы из папки com/blazik/howto
    if os.path.exists(howto_dir):
        collect_files(howto_dir, '.kt', txt_file)
    else:
        print(f"Папка с .kt файлами не найдена: {howto_dir}")

    # Собираем .xml файлы из папки res/layout
    if os.path.exists(layout_dir):
        collect_files(layout_dir, '.xml', txt_file)
    else:
        print(f"Папка с .xml файлами не найдена: {layout_dir}")

print('Все файлы собраны в collected_code.txt')
