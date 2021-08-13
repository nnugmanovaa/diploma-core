INSERT INTO loan.income_info_catalog (type, value)
VALUES ('MARITAL_STATUS', 'Не женат / Не замужем'),
       ('MARITAL_STATUS', 'Женат / Замужем'),
       ('MARITAL_STATUS', 'Вдовец / Вдова'),
       ('MARITAL_STATUS', 'Разведен / Разведена'),
       ('MARITAL_STATUS', 'Гражданский брак');

INSERT INTO loan.income_info_catalog (type, value)
VALUES ('EDUCATION', 'Среднее'),
       ('EDUCATION', 'Среднее специальное'),
       ('EDUCATION', 'Незаконченное высшее'),
       ('EDUCATION', 'Высшее'),
       ('EDUCATION', 'Магистратура, 2-ое высшее, Ученая степень и пр.');

INSERT INTO loan.income_info_catalog (type, value)
VALUES ('EMPLOYMENT', 'Сотрудник компании'),
       ('EMPLOYMENT', 'Индивидуальный предприниматель'),
       ('EMPLOYMENT', 'Безработный'),
       ('EMPLOYMENT', 'Студент'),
       ('EMPLOYMENT', 'Пенсионер'),
       ('EMPLOYMENT', 'Владелец компании'),
       ('EMPLOYMENT', 'Пенсионер (сотрудник компании)'),
       ('EMPLOYMENT', 'Пенсионер (индивидуальный предприниматель)'),
       ('EMPLOYMENT', 'В декретном отпуске');

INSERT INTO loan.income_info_catalog (type, value)
VALUES ('EMPLOYMENT_TYPE', 'Информационные технологии/телекоммуникации'),
       ('EMPLOYMENT_TYPE', 'Финансы, банки, страхование, консалтинг'),
       ('EMPLOYMENT_TYPE', 'ТЭК'),
       ('EMPLOYMENT_TYPE', 'Сервис и Услуги'),
       ('EMPLOYMENT_TYPE', 'Армия'),
       ('EMPLOYMENT_TYPE', 'Охранная деятельность'),
       ('EMPLOYMENT_TYPE', 'Добывающий сектор'),
       ('EMPLOYMENT_TYPE', 'Культура и искусство'),
       ('EMPLOYMENT_TYPE', 'Медицина'),
       ('EMPLOYMENT_TYPE', 'Наука и Образование'),
       ('EMPLOYMENT_TYPE', 'Государственная служба'),
       ('EMPLOYMENT_TYPE', 'Торговля'),
       ('EMPLOYMENT_TYPE', 'Промышленность'),
       ('EMPLOYMENT_TYPE', 'Строительство и недвижимость'),
       ('EMPLOYMENT_TYPE', 'Транспорт'),
       ('EMPLOYMENT_TYPE', 'Другое');

INSERT INTO loan.income_info_catalog (type, value)
VALUES ('EMPLOYMENT_POSITION', 'Директор / Руководитель'),
       ('EMPLOYMENT_POSITION', 'Главный бухгалтер'),
       ('EMPLOYMENT_POSITION', 'Начальник'),
       ('EMPLOYMENT_POSITION', 'Служащий / Специалист'),
       ('EMPLOYMENT_POSITION', 'Рабочий');

INSERT INTO loan.income_info_catalog (type, value)
VALUES ('NUMBER_OF_KIDS', '0'),
       ('NUMBER_OF_KIDS', '1'),
       ('NUMBER_OF_KIDS', '2'),
       ('NUMBER_OF_KIDS', '3'),
       ('NUMBER_OF_KIDS', '4');

INSERT INTO loan.income_info_catalog (type, value)
VALUES ('PERIOD_OF_RESIDENCY', '1'),
       ('PERIOD_OF_RESIDENCY', '2'),
       ('PERIOD_OF_RESIDENCY', '3'),
       ('PERIOD_OF_RESIDENCY', '4'),
       ('PERIOD_OF_RESIDENCY', '5'),
       ('PERIOD_OF_RESIDENCY', '6'),
       ('PERIOD_OF_RESIDENCY', '7');
