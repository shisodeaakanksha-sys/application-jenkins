pipeline{
   agent any
	stages{
	   stage('PULL STAGE'){
		steps{
		   git 'https://github.com/shisodeaakanksha-sys/application-jenkins.git'
		}
	    }
	
	   stage('FRONTEND-DOCKER-BUILD'){
		steps{
		  sh'''
		  cd frontend
		  docker build -t aakankshas3107/easy-frontend:latest .
		  '''
		}
	    }

	  stage('BACKEND-DOCKER-BUILD'){
		steps{
		  sh'''
		  cd backend
		  docker build -t aakankshas3107/easy-backend:latest .
		  '''
		}
	   }

	  stage ('DOCKER-PUSH'){
		steps{
		   withCredentials([
		    usernamePassword(
		      credentialsId: 'dockerhub',
		      usernameVariable:'DOCKER_USER',
		      passwordVariable:'DOCKER_PASS'
		    )
		   ]){
		     sh'''
		     echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
		     docker push aakankshas3107/easy-frontend:latest
		     docker push aakankshas3107/easy-backend:latest
		     docker logout
		     '''
		     }
		}
	  }

	stage('DOCKER-CLEAN') {
            steps {
                sh '''
                docker rmi -f aakanksha3107/easy-frontend:latest || true
                docker rmi -f aakanksha3107/easy-backend:latest || true
                '''
            }
        }

        stage('DEPLOY') {
           steps {
               sh '''
        aws eks update-kubeconfig \
          --region eu-north-1 \
          --name my-cluster

        kubectl get nodes

        kubectl apply -f simple-deploy/ '''
           }
        }
    }
    
}	

