CREATE TABLE "compartilhado" ( Id_Pasta INTEGER NOT NULL REFERENCES pastacompart(id) ,
 Id_User INTEGER NOT NULL REFERENCES usuarios(ID) ,
 Id_Dono INTEGER NOT NULL REFERENCES usuarios(ID) ,
 PRIMARY KEY (Id_Pasta, Id_User,Id_Dono))

CREATE TABLE "pastacompart" ( ID serial PRIMARY KEY UNIQUE,
Path character varying(255) NOT NULL,
Id_Dono INTEGER NOT NULL REFERENCES usuarios(ID) )
