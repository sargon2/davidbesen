#include <stdio.h>
#define keyspace 16
int finaltest(char* m, int* final);
void increment(char* n1);
void calcmatrix(char* n1, char* n2, char matrix[keyspace][keyspace], int* final);
int addupdiag(char matrix[keyspace][keyspace], int startx, int starty);
void calcfinal(char matrix[keyspace][keyspace], int* final);
void printmatrix(char matrix[keyspace][keyspace]);
void printgrid(char matrix[keyspace][keyspace], char* n1, char* n2);
void recurse(char* m, char* n1, char* n2, char matrix[keyspace][keyspace], int* final, int d, int failed);
int bigger(char* m, int* final);
int biggerc(char* m, char* n);
int isone(char* n);
int ism(char* n, char* m);
void copyit(char* from, char* to);
void printnum(char* num);
void printnumi(int* num);
int toobig;
long iters;
int maxmatch;
int len;
int print;
int main(int argc, char* argv[]) {
	char m[keyspace]; // final number to be found
	char n1[keyspace];
	char n2[keyspace];
	char matrix[keyspace][keyspace];
	int final[keyspace];
	char c[2];
	int i, j;
	int status=0;
	int d = keyspace - 1; // current digit being looked at
	iters = 0; maxmatch = 0;
	print = 0;
	//keyspace = strlen(argv[1]);
	// init everything to 0
	printf("keyspace is %d\n", keyspace);
	for(i = 0; i<keyspace;i++) {
		n1[i] = 0;
		n2[i] = 0;
		m[i] = 0;
		final[i] = 0;
		for(j=0;j<keyspace;j++) {
			matrix[i][j] = 0;
		}
	}
	// init n1 and n2 to 000001
	n1[keyspace-1] = 1;
	n2[keyspace-1] = 1;
	//strcpy(m, argv[1]); // don't do this; right-align it with no \0
	/*
	for(i=0;i<keyspace;i++) {
		c[0] = argv[1][i];
		c[1] = '\0';
		m[i] = atoi(c);
		//printf("m[%d] is %d\n", i, m[i]);
	} */
	len = strlen(argv[1]);
	for(i=0;i<len;i++) {
		c[0] = argv[1][i];
		c[1] = '\0';
		m[i + (keyspace - strlen(argv[1]))] = atoi(c);
		//printf("m[%d] is %d\n", i, m[i]);
	}
	calcmatrix(n1, n2, matrix, final);
	/*
	while(!finaltest(m, final)) {
		while(m[d] != final[d] && n2[d] < 9) {
			n2[d]++;
			if(d==keyspace-1) n2[d]++;
			calcmatrix(n1, n2, matrix, final);
			//printf("d is %d\n", d);
			//printmatrix(matrix);
		}
		if(m[d] == final[d] && toobig != 1) {
			//printf("Got here, d is %d!\n", d);
			//printmatrix(matrix);
			//printnum(final);
			d--;
		} else {
			d = keyspace - 1;
			increment(n1);
			increment(n1);
			for(i=0;i<keyspace;i++) n2[i] = 0;
			n2[keyspace-1] = 3;
			calcmatrix(n1, n2, matrix, final);
			//printmatrix(matrix);
		}
		status++; status %= 10000;
		if(status == 0) printnum(n1);
	}
	printnum(n1);
	printnum(n2);
	*/
	recurse(m, n1, n2, matrix, final, keyspace - 1, 0);
	printf("Failed.\n");
}

int nummatch(char* m, int* final) {
	int i;
	int n;
	for(i=0;i<keyspace;i++) {
		if(m[i] == final[i]) n++;
	}
	return n;
}

int biggerc(char* m, char* n) {
	int i;
	for(i=0;i<keyspace;i++) {
		if(m[i] > n[i]) {
			return 0;
		}
		if(m[i] < n[i]) {
			return 1;
		}
	}
	return 0;
}

int bigger(char* m, int* final) {
	int i;
	for(i=0;i<keyspace;i++) {
		if(m[i] > final[i]) {
			return 0;
		}
		if(m[i] < final[i]) {
			return 1;
		}
	}
	return 0;
}

void copyit(char* from, char* to) {
	int i;
	for(i=0;i<keyspace-1;i++) {
		to[i] = from[i];
	}
}

int isone(char* n) {
	int i;
	for(i=0;i<keyspace-1;i++) {
		if(n[i] != 0) return 0;
	}
	if(n[keyspace-1] != 1) return 0;
	return 1;
}

int ism(char* n, char* m) {
	int i;
	for(i=0;i<keyspace;i++) {
		if(n[i] != m[i]) return 0;
	}
	return 1;
}

void recurse(char* m, char* n1, char* n2, char matrix[keyspace][keyspace], int* final, int d, int failed) {
	char n1s[keyspace];
	char n2s[keyspace];
	int done = 0;
	int b, b2;
	//int match;
	copyit(n1, n1s);
	copyit(n2, n2s);
	while(!done) {
		calcmatrix(n1, n2, matrix, final);
		if(bigger(m, final)) failed = 1;
		b=0; b2=0;
		if(biggerc(m, n1)) { failed = 1; b = 1;}
		if(biggerc(m, n2)) { failed = 1; b2 = 1;}
		if((d - (int)len/4) <= (keyspace - len)) {failed = 1; b2 = 1;}
		if(ism(n1, m)) failed = 1;
		if(ism(n2, m)) failed = 1;
		//if(isone(n1)) failed = 1;
		//if(isone(n2)) failed = 1;
		if(toobig) failed = 1;
		/*printnum(n1);
		printnum(n2);
		printf("\n");*/
		/*
		match = nummatch(m, final);
		if(match > maxmatch && failed != 1) {
			maxmatch = match;
			printf("%i iters: ", iters);
			printnum(n1);
			printf(" * ");
			printnum(n2);
			printf(" = ");
			printnumi(final);
			printf("\n");
		} */
		if(print) {
			printnum(n1);
			printf(" * ");
			printnum(n2);
			printf("\n");
		}
		
		if(finaltest(m, final) && failed != 1) {
		//if(finaltest(m, final)) {
			printf("iters is %li\n", iters);
			printnum(n2);
			printf(" * ");
			printnum(n1);
			printf(" = ");
			printnumi(final);
			printf("\n");
			exit(0);
		}
		if(m[d] == final[d] && failed == 0) recurse(m, n1, n2, matrix, final, d-1, 0);
		n1[d]++;
		if(d == keyspace - 1) {
			n1[d]++;
			if(n1[d] == 5) n1[d] = 7;
		}
		if(n1[d] >= 10 || b==1) {
			n2[d]++;
			if(d == keyspace - 1) {
				n2[d]++;
				if(n2[d] == 5) n2[d] = 7;
			}
			n1[d] = 1;
		}
		//if(n2[d] == 10 || toobig == 1) {
		if(n2[d] >= 10 || b2==1) {
		//	n1[d] = 0;
		//	n2[d] = 0;
		//	if(d == keyspace - 1) { n1[d]++; n2[d]++; }
		//	recurse(m, n1, n2, matrix, final, d+1, 1);
			copyit(n1s, n1); copyit(n2s, n2);
			done = 1;
		}
		failed = 0;
	}
}

void calcmatrix(char* n1, char* n2, char matrix[keyspace][keyspace], int* final) {
	int i, j;
	for(i=0;i<keyspace;i++) {
		for(j=0;j<keyspace;j++) {
			matrix[i][j] = n1[i] * n2[j];
		}
	}
	calcfinal(matrix, final);
	iters++;
/*
	printf("----------------\n");
	printf("Calcfinal says: ");
	printnumi(final);
	printgrid(matrix, n1, n2);
*/
}

void calcfinal(char matrix[keyspace][keyspace], int* final) {
	int startx, starty;
	int* p;
	int s;
	int i;
	int pos = keyspace-1;
	// how the hell...
	//
	for(i=0;i<keyspace;i++) final[i] = 0;
	toobig = 0;
	
	startx = keyspace-1;
	starty = keyspace-1;
	while(starty >= 0) {
		final[pos] += addupdiag(matrix, startx, starty);
		starty--; pos--;
		if(pos < 0) {
			starty = -1;
			startx = -1;
		}
	}
	starty = 0;
	startx = keyspace-1;
	while(startx >= 0) {
		final[pos] += addupdiag(matrix, startx, starty);
		startx--; pos--;
		if(pos < 0) {
			starty = -1;
			startx = -1;
		}
	}
	//printf("Before wrapping final...");
	//printnumi(final);
	p = &final[keyspace-1];
	while(p > &final[0]) {
		while(*p > 9 && toobig != 1) {
			s = (int)(*p / 10);
			*p %= 10;
			if(p == &final[0]) {
				toobig = 1;
				for(i=0;i<keyspace;i++) final[i] = 0;
				continue;
			}
			p--;
			*p += s;
		}
		p--;
	}
	//printf("after wrapping final...");
	//printnumi(final);
}

int addupdiag(char matrix[keyspace][keyspace], int startx, int starty) {
	int value = matrix[startx][starty];
	while(startx >= 0 && starty <= keyspace-1) {
		startx--;
		starty++;
		value += matrix[startx][starty];
	}
	//printf("addupdiag returning %d\n", value);
	return value;
}

int finaltest(char* m, int* final) {
	int i;
	for(i=0;i<keyspace;i++)
		if(m[i] != final[i]) return 0;
	return 1;
}

void increment(char* n1) {
	char* pos = &n1[keyspace-1];
	//printf("before incr, pos is %d\n", *pos);
	//printnum(n1);
	(*pos)++;
	while(*pos >= 10) {
		*pos = 0;
		pos--;
		*pos += 1;
	}
	//printf("after incr, pos is %d\n", *pos);
	//printnum(n1);
}

void printgrid(char matrix[keyspace][keyspace], char* n1, char* n2) {
	int i, j;
	for(i=0;i<keyspace;i++) {
		printf(" %d ", n2[i]);
	}
	printf("\n"); fflush(stdout);
	for(i=0;i<keyspace;i++) {
		printf("%d", n1[i]);
		for(j=0;j<keyspace;j++) {
			printf(" %d ", matrix[i][j]);
		}
		printf("\n");
	}
	printf("n1: ");
	printnum(n1);
	printf("\n"); fflush(stdout);
}

void printmatrix(char matrix[keyspace][keyspace]) {
	int i, j;
	for(i=0;i<keyspace;i++) {
		for(j=0;j<keyspace;j++) {
			printf(" %d ", matrix[i][j]);
		}
		printf("\n");
	}
	printf("\n"); fflush(stdout);
}

void printnum(char* num) {
	int i=0;
	while(num[i] == 0) i++;
	for(;i<keyspace;i++) {
		printf("%d", num[i]);
	}
	//printf("\n");
	fflush(stdout);
}

void printnumi(int* num) {
	int i=0;
	while(num[i] == 0) i++;
	for(;i<keyspace;i++) {
		printf("%d", num[i]);
	}
	//printf("\n");
	fflush(stdout);
}
