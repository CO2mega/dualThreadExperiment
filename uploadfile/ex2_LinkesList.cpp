#include <iostream>
#include <cstring>
#include <limits>
#include <cmath>

using namespace std;

#define MAX 100

template<typename T>
struct Node
{
    T data;
    Node* next;
};

template<typename T>
class Stack
{
    private:
        Node<T>* top;
        int size;
        bool isEmpty() {
            return size == 0;
        }

    public:
        Stack() {
            top = new Node<T>;
            top->next = NULL;
            size = 0;
        }
        ~Stack() {
            while(top) {
                Node<T>* temp = top;
                top = top->next;
                delete temp;
            }
        }
        
        void ShowSize() {
            cout << "栈大小：" << size << endl;
            return;
        }

        void push(T data) {
            if(size >= MAX) {
                cout << "栈已满！" << endl;
                return;
            }
            Node<T>* temp = new Node<T>;
            temp->data = data;
            temp->next = top->next;
            top->next = temp;
            size++;
        }
        bool pop(T& e) {
            if(isEmpty()) 
                return false;
            Node<T>* temp = top->next;
            top->next = temp->next;
            size--;
            e = temp->data;
            delete temp;
            return true;
        }
        bool peek(T& e) {
            if(isEmpty()) 
                return false;
            e = top->next->data;
            return true;
        }
};


bool compare(char a, char b) {
    switch(a) {
        case '(': return false;
        case '*': 
        case '/': return true;
        case '+':
        case '-': {
            if(b == '+' || b == '-') 
                return true;
            if(b == '*' || b == '/')
                return false;
        }
        default: return false;   
    }
}
char* changeInf2Suf(char *infix) {
    Stack<char> s;
    char temp;
    char *suffix = new char[MAX];
    int i = 0, j = 0;
    while(infix[i] != '=') {
        if((infix[i] >= '0' && infix[i] <= '9') || infix[i] == '.') {
            suffix[j++] = infix[i];
        }else if(infix[i] == '(') {
            s.push(infix[i]);
        }else if(infix[i] == '+' || infix[i] == '-' || infix[i] == '*' || infix[i] == '/' ) {
            while(s.peek(temp) && compare(temp, infix[i])) {
                s.pop(temp);
                suffix[j++] = temp;
            }
            s.push(infix[i]);
            if(suffix[j-1]>='0' && suffix[j-1]<='9') {
                suffix[j++] = '#';
            }
        }else if(infix[i] == ')') {
            while(s.pop(temp) && temp != '(') {
                suffix[j++] = temp;
            }
            if(suffix[j-1]>='0' && suffix[j-1]<='9')
                suffix[j++] = '#';
        }
        i++;
    }
    while (s.pop(temp)) {
        suffix[j++] = temp;
    }
    suffix[j++] = '=';
    suffix[j] = '\0';
    return suffix;
}

double calculate(char *suffix) {
    Stack<double> s;
    char temp;
    int i = 0;
    bool flag = false;
    while(suffix[i] != '=') {
        if(suffix[i]>='0' && suffix[i]<='9') {
            if(flag) {
                double a;
                s.pop(a);
                s.push(a*10+suffix[i]-'0');
            }else {
                s.push(suffix[i]-'0');
            }
            flag = true;
        }else if(suffix[i] == '.') {
            i++;
            double fl = 0;
            double a;
            int n = 1;
            while(suffix[i]>='0' && suffix[i]<='9') {
                fl += (suffix[i]-'0')*pow(0.1, n++);
                i++;
            }
            i--;
            s.pop(a);
            s.push(a+fl);
        }else if(suffix[i] == '#') {
            flag = false;
        }else {
            double a, b;
            flag = false; 
            s.pop(b);
            s.pop(a);
            switch(suffix[i]) {
                case '+': s.push(a+b); break;
                case '-': s.push(a-b); break;
                case '*': s.push(a*b); break;
                case '/': s.push(a/b); break;
            }
        }
        i++;
    }
    double a;
    s.peek(a);
    return a;
}

int main() {
    system("chcp 65001");
    char infix[MAX];
    cout << "请输入中缀表达式: ";
    cin.getline(infix, MAX);
    char *suffix = changeInf2Suf(infix);
    cout << suffix << endl;
    cout << calculate(suffix) << endl;
    system("pause");
    // delete suffix;
    // suffix = new char[MAX];
    // cout<<"请输入后缀表达式：";
    // cin.getline(suffix, MAX);
    // cout << calculate(suffix) << endl;
    return 0;
}
