#include <stdio.h>
#include <string.h>

void wordToBinary(char *word) {
    int i, j;
    for (i = 0; i < strlen(word); i++) {
        char character = word[i];
        for (j = 8; j > 0; j--) {
            printf("%d", (character >> (j - 1)) & 1);
        }
        printf(" ");
    }
    printf("\n");
}

int main() {
    char word[] = "hello";
    printf("Binary representation of '%s':\n", word);
    wordToBinary(word);
    return 0;
}
