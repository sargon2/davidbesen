#include <stdio.h>
#include <string.h>

#define ORDER 16 // 16 seemed to provide the most reliable performance

#include <windows.h>

struct node_t {
	int count;
	char key[ORDER*2-1][11]; // phone number...
	node_t* child[ORDER*2];
	bool leaf;
	node_t* up; // ahackahackahack
};

node_t* head;

void init();
void insert(char* key);
void insert_node(node_t* node, char* key, int index, node_t* child);
node_t* search(char* key);
int search_node(node_t* node, char* key);
int search_node_b(node_t* node, char* key);
void split_node(node_t* node);

int main(int argc, char** argv) {
	long MAX = 1001000000;
	long MIN = 1000000000;
	DWORD ticks;
	char phone[11];
	node_t* asdf;
	long i;
	ticks = GetTickCount();
	for(i = MAX; i >= MIN; i--) {
		sprintf(phone, "%li", i);
		insert(phone);
		asdf = search(phone);
		if(asdf == NULL)
		{
			printf("Danger, will robinson! %s\n", phone);
		}
	}
	printf("Inserts took %d\n", GetTickCount() - ticks);
	ticks = GetTickCount();
	for(i = MIN; i <= MAX; i++) {
		sprintf(phone, "%li", i);
		asdf = search(phone);
		if(asdf == NULL) {
			printf("Not found!!!!! %s\n", phone);
		}
	}
	printf("searches took %d\n", GetTickCount() - ticks);
/*	if(asdf == NULL)
		printf("Not found\n");
	else
		printf("%s\n", asdf->key[0]);
*/
	return 1;
}

void init() {
	static bool called = false;
	if(called == false) {
		called = true;
		head = new node_t;
		head->count = 0;
		head->leaf = true;
		head->up = NULL;
	}
}


void insert(char* key) {
	node_t* curr;
	int index;

	init();

	curr = head;
	while(curr->leaf == false) {
		index = search_node_b(curr, key);
		if(index == -1)
			return; // key exists already
		curr = curr->child[index];
	}
	index = search_node_b(curr, key);
	if(index == -1)
		return; // key exists already

	insert_node(curr, key, index, NULL);

	if(curr->count == ORDER*2-1) {
		// split...
		split_node(curr);
	}
}

void insert_node(node_t* node, char* key, int index, node_t* child) { // inserts data into a node
	int i;
	if(index < 0) {
		printf("Index less than 0 passed to insert_node\n");
	}
	node->child[node->count+1] = node->child[node->count];
	for(i=node->count;i>index;i--) {
		strcpy(node->key[i], node->key[i-1]);
		node->child[i] = node->child[i-1];
	}
	strcpy(node->key[index], key);
	node->child[index+1] = child;
	node->count++;
}

node_t* search(char* key) {
	node_t* curr;
	int index;

	init();

	curr = head;
	while(curr->leaf == false) {
		index = search_node_b(curr, key);
		if(index == -1)
			return curr; // key exists
		curr = curr->child[index];
	}
	index = search_node_b(curr, key);
	if(index == -1)
		return curr; // key exists
	return NULL;
}

int search_node(node_t* node, char* key) { // returns pointer index or -1 if key exists
	int i;
	int c;
	for(i=0;i<node->count;i++) {
		c = strcmp(node->key[i], key);
		if(c == 0) return -1;
		if(c > 0) return i;
	}
	return i;
}

int search_node_b(node_t* node, char* key) {
	int c;
	int max, min;
	int cut = node->count - 1;
	max = node->count;
	min = 0;
	if(node->count == 0) return 0;
	while(min != max) {
		c = strcmp(node->key[cut], key);
		if(c == 0) return -1;
		if(c < 0) min = cut + 1;
		if(c > 0) max = cut;
		cut = (min + max) >> 1;
	}
	return cut;
}

void split_node(node_t* node) { // will split all the way up to the root if needed
	int i;
	node_t* newnode;
	if(node->up == NULL) { // root
		newnode = new node_t;
		newnode->count = ORDER-1;
//		newnode->up = node->up;
		newnode->leaf = node->leaf;
		for(i=0;i<ORDER-1;i++) {
			strcpy(newnode->key[i], node->key[i+ORDER]);
			newnode->child[i] = node->child[i+ORDER];
			if(newnode->leaf == false) newnode->child[i]->up = newnode;
		}
		newnode->child[ORDER-1] = node->child[ORDER*2-1];
		if(newnode->leaf == false) newnode->child[ORDER-1]->up = newnode;

		head = new node_t;
		head->count = 1;
		head->leaf = false;
		head->up = NULL;
		strcpy(head->key[0], node->key[ORDER-1]);
		head->child[0] = node;
		head->child[1] = newnode;
		node->up = head;
		newnode->up = head;
		node->count = ORDER-1;

	} else {
		newnode = new node_t;
		newnode->count = ORDER-1;
		newnode->up = node->up;
		newnode->leaf = node->leaf;
		for(i=0;i<ORDER-1;i++) {
			strcpy(newnode->key[i], node->key[i+ORDER]);
			newnode->child[i] = node->child[i+ORDER];
			if(newnode->leaf == false) newnode->child[i]->up = newnode;
		}
		newnode->child[ORDER-1] = node->child[ORDER*2-1];
		if(newnode->leaf == false) newnode->child[ORDER-1]->up = newnode;
		insert_node(node->up, node->key[ORDER-1], search_node_b(node->up, node->key[ORDER-1]), newnode);
		node->count = ORDER-1;
		if(node->up->count == ORDER*2-1)
			split_node(node->up);
	}
}
