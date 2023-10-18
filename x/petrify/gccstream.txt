#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

int main(int argc, char **argv) {
	FILE *fp, *fp2;
	char name[L_tmpnam];
	char filename[L_tmpnam+2];
	char cmd[1024];
	char line[1024];
	char args[1024];
	char *p;
	int i;
	int argnum;

	argnum = 0;
	fp = NULL;
	while(fp == NULL) {
		argnum++;
		if(argnum >= argc) {
			printf("Program not found.\n");
			return 0;
		}
		fp = fopen(argv[argnum], "r");
	}

	tmpnam(name); // use mkstemp/mkstemps
	sprintf(filename, "%s.c", name);
	if((fp2 = fopen(filename, "w")) == NULL) {
		perror(filename);
		return 0;
	}
	line[0] = '\0';
	fgets(line, 1024, fp);
	if(strncmp(line, "#!", 2) == 0) {
		p = strchr(line, ' ');
		if(p != NULL) {
			strcpy(args, p+1);
			args[strlen(args)-1] = '\0';
		} else {
			args[0] = '\0';
		}
		fprintf(fp2, "\n"); // line up error numbers
	} else {
		args[0] = '\0';
		fprintf(fp2, "%s", line);
	}
	while(!feof(fp)) {
		line[0] = '\0';
		fgets(line, 1024, fp);
		fprintf(fp2, "%s", line);
	}

	fclose(fp);
	fclose(fp2);

	fflush(stdout);
	sprintf(cmd, "gcc %s %s.c -o %s", args, name, name); system(cmd);
	sprintf(cmd, "%s", name);
	for(i=argnum+1;i<argc;i++) {
		strcat(cmd, " ");
		strcat(cmd, argv[i]);
	} system(cmd);
	sprintf(cmd, "%s", name); unlink(cmd);
	sprintf(cmd, "%s.c", name); unlink(cmd);
	return 1;
}
